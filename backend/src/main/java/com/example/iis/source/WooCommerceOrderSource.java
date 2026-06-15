package com.example.iis.source;

import com.example.iis.config.AppProperties;
import com.example.iis.dto.BillingDto;
import com.example.iis.dto.LineItemDto;
import com.example.iis.dto.OrderDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Order source that proxies the public WooCommerce REST API (endpoint #99,
 * orders) using Consumer Key + Consumer Secret HTTP Basic authentication.
 * Active when {@code app.order-source=woocommerce}.
 *
 * <p>JSON is handled with a private Jackson 2 mapper and exchanged as String
 * bodies, so it is independent of the Jackson 3 converters Spring Boot 4 uses.
 */
@Component
@ConditionalOnProperty(name = "app.order-source", havingValue = "woocommerce")
public class WooCommerceOrderSource implements OrderSource {

    private static final String ORDERS_PATH = "/wp-json/wc/v3/orders";

    private final RestClient client;
    private final ObjectMapper mapper;

    public WooCommerceOrderSource(AppProperties props, ObjectMapper mapper) {
        this.mapper = mapper;
        AppProperties.WooCommerce wc = props.getWoocommerce();
        String basic = Base64.getEncoder().encodeToString(
                (wc.getConsumerKey() + ":" + wc.getConsumerSecret()).getBytes(StandardCharsets.UTF_8));
        this.client = RestClient.builder()
                .baseUrl(wc.getBaseUrl())
                .defaultHeader("Authorization", "Basic " + basic)
                .build();
    }

    @Override
    public List<OrderDto> findAll() {
        JsonNode array = readTree(client.get()
                .uri(ORDERS_PATH + "?per_page=100")
                .retrieve()
                .body(String.class));
        List<OrderDto> result = new ArrayList<>();
        if (array != null && array.isArray()) {
            for (JsonNode node : array) {
                result.add(fromWoo(node));
            }
        }
        return result;
    }

    @Override
    public Optional<OrderDto> findById(Long id) {
        JsonNode node = readTree(client.get()
                .uri(ORDERS_PATH + "/" + id)
                .retrieve()
                .body(String.class));
        return Optional.ofNullable(node).map(this::fromWoo);
    }

    @Override
    public OrderDto create(OrderDto dto) {
        JsonNode node = readTree(client.post()
                .uri(ORDERS_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(write(toWoo(dto)))
                .retrieve()
                .body(String.class));
        return fromWoo(node);
    }

    @Override
    public Optional<OrderDto> update(Long id, OrderDto dto) {
        JsonNode node = readTree(client.put()
                .uri(ORDERS_PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(write(toWoo(dto)))
                .retrieve()
                .body(String.class));
        return Optional.ofNullable(node).map(this::fromWoo);
    }

    @Override
    public boolean delete(Long id) {
        client.delete()
                .uri(ORDERS_PATH + "/" + id + "?force=true")
                .retrieve()
                .toBodilessEntity();
        return true;
    }

    @Override
    public String name() {
        return "woocommerce (public REST API)";
    }

    // --- JSON helpers ----------------------------------------------------

    private JsonNode readTree(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return mapper.readTree(json);
        } catch (Exception e) {
            throw new IllegalStateException("Invalid JSON from WooCommerce: " + e.getMessage(), e);
        }
    }

    private String write(ObjectNode node) {
        try {
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalStateException("Could not serialize order payload: " + e.getMessage(), e);
        }
    }

    // --- mapping helpers -------------------------------------------------

    private OrderDto fromWoo(JsonNode n) {
        OrderDto dto = new OrderDto();
        dto.setId(n.path("id").isMissingNode() ? null : n.path("id").asLong());
        dto.setNumber(text(n, "number"));
        dto.setStatus(text(n, "status"));
        dto.setCurrency(text(n, "currency"));
        if (n.hasNonNull("total")) {
            dto.setTotal(new BigDecimal(n.get("total").asText()));
        }
        dto.setCustomerNote(text(n, "customer_note"));
        dto.setDateCreated(text(n, "date_created"));

        JsonNode b = n.path("billing");
        BillingDto billing = new BillingDto();
        billing.setFirstName(text(b, "first_name"));
        billing.setLastName(text(b, "last_name"));
        billing.setEmail(text(b, "email"));
        billing.setPhone(text(b, "phone"));
        billing.setCity(text(b, "city"));
        billing.setCountry(text(b, "country"));
        dto.setBilling(billing);

        List<LineItemDto> items = new ArrayList<>();
        for (JsonNode li : n.path("line_items")) {
            LineItemDto item = new LineItemDto();
            item.setProductId(li.path("product_id").asLong());
            item.setName(text(li, "name"));
            item.setQuantity(li.path("quantity").asInt());
            if (li.hasNonNull("price")) {
                item.setPrice(new BigDecimal(li.get("price").asText()));
            }
            if (li.hasNonNull("subtotal")) {
                item.setSubtotal(new BigDecimal(li.get("subtotal").asText()));
            }
            items.add(item);
        }
        dto.setLineItems(items);
        return dto;
    }

    private ObjectNode toWoo(OrderDto dto) {
        ObjectNode root = mapper.createObjectNode();
        if (dto.getStatus() != null) root.put("status", dto.getStatus());
        if (dto.getCurrency() != null) root.put("currency", dto.getCurrency());
        if (dto.getCustomerNote() != null) root.put("customer_note", dto.getCustomerNote());

        BillingDto b = dto.getBilling();
        if (b != null) {
            ObjectNode billing = root.putObject("billing");
            billing.put("first_name", nz(b.getFirstName()));
            billing.put("last_name", nz(b.getLastName()));
            billing.put("email", nz(b.getEmail()));
            billing.put("phone", nz(b.getPhone()));
            billing.put("city", nz(b.getCity()));
            billing.put("country", nz(b.getCountry()));
        }

        ArrayNode items = root.putArray("line_items");
        if (dto.getLineItems() != null) {
            for (LineItemDto li : dto.getLineItems()) {
                ObjectNode item = items.addObject();
                if (li.getProductId() != null) item.put("product_id", li.getProductId());
                if (li.getName() != null) item.put("name", li.getName());
                item.put("quantity", li.getQuantity());
            }
        }
        return root;
    }

    private static String text(JsonNode node, String field) {
        JsonNode v = node.path(field);
        return v.isMissingNode() || v.isNull() ? null : v.asText();
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }
}
