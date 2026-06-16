package com.example.client.service;

import com.example.client.config.ClientProperties;
import com.example.client.model.ImportResponseView;
import com.example.client.model.OrderView;
import com.example.client.model.SoapOrderView;
import com.example.client.model.TokenResponse;
import com.example.client.model.ValidationView;
import com.example.client.session.UserSession;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class BackendClient {

    private static final String SOAP_NS = "http://example.com/iis/orders-soap";

    private final RestClient rest;
    private final UserSession session;

    public BackendClient(ClientProperties props, UserSession session) {
        this.session = session;
        this.rest = RestClient.builder().baseUrl(props.getBaseUrl()).build();
    }

    private String bearer() {
        return "Bearer " + session.getAccessToken();
    }

    public TokenResponse login(String username, String password) {
        return rest.post()
                .uri("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("username", username, "password", password))
                .retrieve()
                .body(TokenResponse.class);
    }

    public List<OrderView> listOrders() {
        OrderView[] arr = rest.get()
                .uri("/api/orders")
                .header("Authorization", bearer())
                .retrieve()
                .body(OrderView[].class);
        return arr == null ? List.of() : Arrays.asList(arr);
    }

    public OrderView getOrder(Long id) {
        return rest.get()
                .uri("/api/orders/{id}", id)
                .header("Authorization", bearer())
                .retrieve()
                .body(OrderView.class);
    }

    public void createOrder(OrderView order) {
        rest.post()
                .uri("/api/orders")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(order)
                .retrieve()
                .toBodilessEntity();
    }

    public void updateOrder(Long id, OrderView order) {
        rest.put()
                .uri("/api/orders/{id}", id)
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(order)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteOrder(Long id) {
        rest.delete()
                .uri("/api/orders/{id}", id)
                .header("Authorization", bearer())
                .retrieve()
                .toBodilessEntity();
    }

    public String activeSource() {
        Map<?, ?> body = rest.get()
                .uri("/api/source")
                .header("Authorization", bearer())
                .retrieve()
                .body(Map.class);
        return body == null ? "?" : String.valueOf(body.get("source"));
    }

    public ImportResponseView importOrders(byte[] xml, String xmlName, byte[] json, String jsonName) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        if (xml != null && xml.length > 0) {
            builder.part("xml", named(xml, xmlName)).contentType(MediaType.APPLICATION_XML);
        }
        if (json != null && json.length > 0) {
            builder.part("json", named(json, jsonName)).contentType(MediaType.APPLICATION_JSON);
        }
        return rest.post()
                .uri("/api/import/orders")
                .header("Authorization", bearer())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .exchange((request, response) -> response.bodyTo(ImportResponseView.class));
    }

    private static ByteArrayResource named(byte[] data, String filename) {
        return new ByteArrayResource(data) {
            @Override
            public String getFilename() {
                return filename;
            }
        };
    }

    public List<SoapOrderView> soapSearch(String term) throws Exception {
        String envelope = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" \
                xmlns:ord="http://example.com/iis/orders-soap">
                  <soapenv:Body>
                    <ord:SearchOrdersRequest><ord:term>%s</ord:term></ord:SearchOrdersRequest>
                  </soapenv:Body>
                </soapenv:Envelope>""".formatted(xmlEscape(term));

        String response = rest.post()
                .uri("/ws")
                .contentType(MediaType.TEXT_XML)
                .body(envelope)
                .retrieve()
                .body(String.class);

        return parseSoapResponse(response);
    }

    private List<SoapOrderView> parseSoapResponse(String xml) throws Exception {
        List<SoapOrderView> results = new ArrayList<>();
        if (xml == null) {
            return results;
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder()
                .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        NodeList orders = doc.getElementsByTagNameNS(SOAP_NS, "order");
        for (int i = 0; i < orders.getLength(); i++) {
            Element o = (Element) orders.item(i);
            SoapOrderView v = new SoapOrderView();
            v.setNumber(local(o, "number"));
            v.setStatus(local(o, "status"));
            v.setCurrency(local(o, "currency"));
            v.setTotal(local(o, "total"));
            v.setCustomerName(local(o, "customerName"));
            v.setEmail(local(o, "email"));
            v.setCity(local(o, "city"));
            results.add(v);
        }
        return results;
    }

    private static String local(Element parent, String name) {
        NodeList n = parent.getElementsByTagNameNS(SOAP_NS, name);
        return n.getLength() > 0 ? n.item(0).getTextContent() : "";
    }

    private static String xmlEscape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    public ValidationView validateXml() {
        return rest.get()
                .uri("/api/xml/validate")
                .header("Authorization", bearer())
                .retrieve()
                .body(ValidationView.class);
    }

    public String generatedXml() {
        return rest.get()
                .uri("/api/xml/orders")
                .header("Authorization", bearer())
                .retrieve()
                .body(String.class);
    }

    public String graphql(String query) {
        return rest.post()
                .uri("/graphql")
                .header("Authorization", bearer())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("query", query))
                .exchange((request, response) -> response.bodyTo(String.class));
    }
}
