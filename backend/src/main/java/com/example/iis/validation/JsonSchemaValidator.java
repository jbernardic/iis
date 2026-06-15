package com.example.iis.validation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

/**
 * Validates a JSON order document against {@code schema/order-schema.json}
 * (JSON Schema draft 2020-12) for Part 1.
 */
@Component
public class JsonSchemaValidator {

    private final ObjectMapper mapper;
    private JsonSchema schema;

    public JsonSchemaValidator(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    void init() throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream in = new ClassPathResource("schema/order-schema.json").getInputStream()) {
            this.schema = factory.getSchema(in);
        }
    }

    /** Returns the list of validation problems (empty = valid). */
    public List<String> validate(byte[] json) {
        JsonNode node;
        try {
            node = mapper.readTree(json);
        } catch (Exception e) {
            return List.of("Malformed JSON: " + e.getMessage());
        }
        Set<ValidationMessage> messages = schema.validate(node);
        return messages.stream()
                .map(ValidationMessage::getMessage)
                .sorted()
                .toList();
    }
}
