package com.example.iis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 4 uses Jackson 3 ({@code tools.jackson}) for the web layer, so no
 * Jackson 2 ({@code com.fasterxml.jackson}) {@code ObjectMapper} bean is created
 * automatically. The networknt JSON Schema validator (Part 1) and the
 * WooCommerce client are built on Jackson 2, so we expose one explicitly. The
 * two types are distinct, so this does not collide with the Jackson 3 bean.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper jackson2ObjectMapper() {
        return new ObjectMapper();
    }
}
