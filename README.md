# IIS Project — WooCommerce Orders (endpoint #99)

A backend + frontend system built around the **WooCommerce REST API _orders_ endpoint**
(`/wp-json/wc/v3/orders`). It implements all six required functionalities: a validating
REST import (XSD + JSON Schema), a SOAP service with XPath filtering, Jakarta XML
validation, a gRPC weather service (DHMZ), a custom REST API with JWT + GraphQL and a
public/custom source switch, and a web client with two user roles.

> Authentication for the *public* WooCommerce API is Consumer Key + Consumer Secret
> (HTTP Basic), configured under *WordPress → WooCommerce → Settings → Advanced → REST API*.

---

## Tech stack

| | |
|---|---|
| Language / JDK | Java 26 |
| Build | Maven (multi-module reactor) |
| Framework | Spring Boot 4.1 (Spring Framework 7) |
| Persistence | Spring Data JPA + H2 (in-memory) |
| SOAP | Spring Web Services (contract-first, JAXB) |
| gRPC | grpc-java 1.80 + protobuf 4.34 (`grpc-netty-shaded`) |
| GraphQL | Spring for GraphQL |
| Security | Spring Security + JWT (jjwt 0.12) |
| Validation | `javax.xml.validation` (XSD), networknt json-schema-validator (JSON Schema) |
| Client UI | Spring Boot + Thymeleaf + Spring Security |

### Modules

```
iis/
├── proto/      shared gRPC/protobuf stubs (weather.proto + generated Java)
├── backend/    all server-side services (REST, SOAP, gRPC, GraphQL, JWT, DB)  → :8080 (+ gRPC :9090)
└── client/     Thymeleaf web client with two roles                             → :8081
```

---

## Prerequisites & running

Requires **JDK 17+** (built and tested with OpenJDK 26). Set `JAVA_HOME` to your JDK 26.

### From IntelliJ IDEA
1. Open the project (it imports from `pom.xml` as a Maven project).
2. *File → Project Structure → Project* → set the SDK to JDK 26.
3. *Settings → Build, Execution, Deployment → Build Tools → Maven → Runner* → set the JRE to the
   project SDK (JDK 26).
4. Run **`IisApplication`** (backend) — green ▶ in the gutter; it starts on `:8080` + gRPC `:9090`.
5. Run **`ClientApplication`** (client) — it starts on `:8081`.
   (You can also use the Maven tool window: `backend → Plugins → spring-boot → spring-boot:run`.)

### From the command line (Maven)
```bash
# build everything (also runs tests)
mvn clean package

# run the backend (Tomcat :8080, gRPC :9090)
mvn -pl backend spring-boot:run

# in a second terminal, run the web client (:8081)
mvn -pl client spring-boot:run
```

Then open **http://localhost:8081** and log in.

### Demo users (seeded on first start)

| Username | Password | Role | Can do |
|----------|----------|------|--------|
| `admin`  | `admin123`  | full access | all endpoints (GET/POST/PUT/DELETE, import, mutations) |
| `reader` | `reader123` | read-only   | GET-style operations only |

H2 console (backend): http://localhost:8080/h2-console — JDBC URL `jdbc:h2:mem:iis`, user `sa`.

---

## The six functionalities

### 1 · REST import with XSD + JSON Schema validation
`POST /api/import/orders` (multipart) accepts an **XML** file and/or a **JSON** file describing
an order. Each is validated — XML against [`order.xsd`](backend/src/main/resources/schema/order.xsd),
JSON against [`order-schema.json`](backend/src/main/resources/schema/order-schema.json) — and only
**valid** documents are stored in the database. Validation errors are returned per file
(HTTP 422 when anything is invalid).

* Code: `web/ImportController`, `service/OrderImportService`, `validation/XmlSchemaValidator`,
  `validation/JsonSchemaValidator`.
* UI: **Import (1)** (full access only).
* Sample files: [`samples/`](samples) (`order-valid.xml`, `order-invalid.xml`, `order-valid.json`, `order-invalid.json`).

```bash
TOKEN=$(curl -s -XPOST localhost:8080/api/auth/login -H 'Content-Type: application/json' \
        -d '{"username":"admin","password":"admin123"}' | jq -r .accessToken)
curl -s -XPOST localhost:8080/api/import/orders -H "Authorization: Bearer $TOKEN" \
     -F xml=@samples/order-valid.xml -F json=@samples/order-invalid.json
```

### 2 · SOAP service with XPath filtering
A SOAP service receives a search **term**. The backend first **generates an XML file**
(`<orders>`) from the order data returned by the REST layer, then filters it with **XPath**
(case-insensitive, diacritics-aware) and returns the matching records.

* Code: `soap/OrdersEndpoint`, `service/OrderXmlService` (generation), `service/OrderSearchService`
  (XPath), `config/SoapConfig`. Contract: [`orders-soap.xsd`](backend/src/main/resources/schema/orders-soap.xsd).
* WSDL: http://localhost:8080/ws/orders.wsdl
* UI: **SOAP (2)**.

### 3 · Jakarta XML validation of the prepared file
`GET /api/xml/validate` regenerates the prepared `<orders>` file and validates it against the
XSD using **Jakarta XML validation** (`javax.xml.validation`), returning the validation messages.
`GET /api/xml/orders` returns the generated XML itself.

* Code: `service/OrderXmlService#validateGeneratedFile`, `web/XmlController`.
* UI: **XML Validate (3)**.

### 4 · gRPC weather server (DHMZ)
A gRPC server fetches DHMZ data (https://vrijeme.hr/hrvatska_n.xml) and returns the current
temperature for every city whose name **contains** the query (so a partial name returns all
matches). Reachable from the web client.

* Proto: [`proto/src/main/proto/weather.proto`](proto/src/main/proto/weather.proto).
* Code: `grpc/WeatherServiceImpl`, `grpc/DhmzWeatherService`, `grpc/GrpcServer` (port 9090);
  client side `client/.../service/WeatherGrpcClient`.
* UI: **Weather (4)** — try `zag`, `split`, `osij`.

### 5 · Custom REST API + JWT + GraphQL + source switch
A custom REST API exposes all four CRUD operations on the application database, secured with
**JWT access + refresh tokens**, and the same data is also available over **GraphQL**. A config
**switch** (`app.order-source`) flips the data source between the local DB (`custom`) and the
public WooCommerce API (`woocommerce`).

* REST: `web/OrderRestController` (`/api/orders` GET/POST/PUT/DELETE), `web/AuthController`
  (`/api/auth/login`, `/api/auth/refresh`).
* GraphQL: `graphql/OrderGraphQlController`, schema
  [`schema.graphqls`](backend/src/main/resources/graphql/schema.graphqls); GraphiQL at
  http://localhost:8080/graphiql.
* Switch: `source/OrderSource` with `DbOrderSource` / `WooCommerceOrderSource`
  (`@ConditionalOnProperty`), facade `service/OrderService`, reported at `GET /api/source`.
* Security: `config/SecurityConfig`, `security/JwtService`, `security/JwtAuthenticationFilter`.
  GET = read-only **or** full; writes & GraphQL mutations = full only.
* UI: **Orders** and **GraphQL (5)**.

Flip to the public WooCommerce API:

```bash
./gradlew :backend:bootRun --args='--app.order-source=woocommerce \
  --app.woocommerce.base-url=https://your-store.example.com \
  --app.woocommerce.consumer-key=ck_xxx --app.woocommerce.consumer-secret=cs_xxx'
```

### 6 · Client web app with two roles
A Thymeleaf web client (`client` module) with a GUI to call every service above. It authenticates
against the backend JWT login and maps the returned role to UI/authorization:

* **read-only** (`reader`) — can call GET-style operations only; write buttons are hidden and
  write URLs are blocked (HTTP 403).
* **full access** (`admin`) — can call everything.

Code: `client/.../web/*` controllers, `client/.../service/BackendClient` (REST/SOAP/GraphQL),
`client/.../service/WeatherGrpcClient` (gRPC), `client/.../config/WebSecurityConfig`,
`client/.../security/BackendAuthenticationProvider`.

---

## Notes / design decisions

* **Spring Boot 4 uses Jackson 3** (`tools.jackson`) for the web layer. The networknt JSON Schema
  validator and the WooCommerce client are Jackson 2 based, so a Jackson 2 `ObjectMapper` is
  provided explicitly (`config/JacksonConfig`) and the WooCommerce client exchanges String bodies.
* **gRPC stubs are generated and committed** under `proto/src/main/java` (no protobuf build plugin,
  which keeps the build simple and self-contained). To regenerate after editing `weather.proto`,
  run `protoc` (4.34.x) with the `protoc-gen-grpc-java` plugin (1.80.x) into `proto/src/main/java`.
* The H2 database is **in-memory**; data (users + 5 sample orders) is re-seeded on each start
  (`config/DataSeeder`).
* CSRF is disabled on the client to keep the multipart upload demo simple.

## Verified

`mvn clean package` passes. All six parts were smoke-tested end-to-end (login/JWT, REST CRUD,
role enforcement, multipart import with validation errors, SOAP/XPath search, Jakarta XML
validation, gRPC weather lookup, GraphQL query/mutation) against the running backend and through
the web client.
