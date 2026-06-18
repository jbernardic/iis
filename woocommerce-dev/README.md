# Lokalni WooCommerce za razvoj (besplatno)

Podiže pravi **WordPress + WooCommerce + MySQL** lokalno u Dockeru, tako da
"javna" strana prekidača (`app.order-source=woocommerce`) ima na što se spojiti
— bez plaćene trgovine i bez ikakve izmjene koda backenda.

| Servis | URL / port | Login |
|--------|-----------|-------|
| WordPress / WooCommerce | <http://localhost:8090> | — |
| WP admin | <http://localhost:8090/wp-admin> | `admin` / `admin123` |
| MySQL | interni (kontejner `wc-db`) | `wordpress` / `wordpress` |

> Portovi 8080 (backend) i 8081 (klijent) ostaju slobodni — WooCommerce je na **8090**.

## Preduvjeti
- Docker Desktop (uključuje `docker compose`).

## 1. Pokretanje
Iz mape `woocommerce-dev/`:

```powershell
docker compose up -d
```

Prvi put traje ~1–2 min (skida slike, instalira WordPress + WooCommerce, dodaje
2 demo proizvoda). Tijek instalacije:

```powershell
docker compose logs -f wpcli
```

Kad vidiš `WordPress + WooCommerce ready`, gotovo je. (`wpcli` kontejner se
normalno ugasi nakon posla — to je očekivano.)

## 2. Generiranje REST ključeva

### A) Automatski (preporuka)
Ispisuje gotove linije za `application.properties`:

```powershell
docker compose run --rm --entrypoint wp wpcli eval-file /scripts/create-api-key.php
```

Primjer ispisa:

```
app.order-source=woocommerce
app.woocommerce.base-url=http://localhost:8090
app.woocommerce.consumer-key=ck_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
app.woocommerce.consumer-secret=cs_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

### B) Ručno (kroz admin)
1. <http://localhost:8090/wp-admin> → **WooCommerce → Settings → Advanced → REST API**.
2. **Add key** → opis npr. `IIS`, korisnik `admin`, **Permissions: Read/Write** → **Generate API key**.
3. Prepiši prikazane **Consumer key** (`ck_…`) i **Consumer secret** (`cs_…`) — vide se **samo jednom**.

## 3. Spajanje s aplikacijom
U `backend/src/main/resources/application.properties` postavi:

```properties
app.order-source=woocommerce
app.woocommerce.base-url=http://localhost:8090
app.woocommerce.consumer-key=ck_...   # iz koraka 2
app.woocommerce.consumer-secret=cs_...
```

Restartaj backend (properties se čitaju pri pokretanju).

## 4. Provjera
- Aktivni izvor: `GET http://localhost:8080/api/source` → treba javiti woocommerce.
- Dohvat narudžbi: `GET http://localhost:8080/api/orders` (na početku prazno — WooCommerce još nema narudžbi).
- Kroz klijent (`admin`) napravi **POST** narudžbe → kreira se u WooCommerce-u; vidljiva i u **wp-admin → WooCommerce → Orders**.

Brza izravna provjera WooCommerce API-ja (zaobilazi backend):

```powershell
curl.exe -u "ck_...:cs_..." "http://localhost:8090/wp-json/wc/v3/orders"
```

## 5. Zaustavljanje / reset
```powershell
docker compose stop          # pauza, podaci ostaju
docker compose down          # ugasi i obriši kontejnere (volumeni ostaju)
docker compose down -v       # PUNI reset: briše i bazu/WordPress (volumene)
```

## Kako radi (ukratko)
- `docker-compose.yml` — MySQL + WordPress + jednokratni `wpcli` instalater (WordPress core, WooCommerce, pretty permalinks za `/wp-json/…`, demo proizvodi).
- `mu-plugins/force-ssl-for-rest.php` — WooCommerce prihvaća **HTTP Basic Auth** samo kad misli da je veza SSL. Na lokalnom HTTP-u ovaj mali mu-plugin označi REST zahtjeve kao "sigurne" da prođe `Authorization: Basic` header koji backend šalje. **Samo za razvoj.**
- `scripts/create-api-key.php` — generira Read/Write ključeve i ispiše ih u obliku za `application.properties`.

## Troubleshooting
- **401 na `/wp-json/wc/v3/orders`** — provjeri da je `mu-plugins` mount aktivan (`docker compose up -d` iz ove mape) i da ključevi imaju **Read/Write** ovlasti.
- **404 na `/wp-json/…`** — permalinks nisu "pretty". Ponovno pokreni instalater: `docker compose run --rm --entrypoint wp wpcli rewrite structure '/%postname%/' --hard`.
- **Port 8090 zauzet** — promijeni `8090:80` u `docker-compose.yml` i uskladi `base-url`.
- **Vraćanje na bazu** — za normalan razvoj samo postavi `app.order-source=custom` (H2). WooCommerce ne mora biti pokrenut.
