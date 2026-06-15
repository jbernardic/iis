package com.example.iis.web;

import com.example.iis.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Reports which order source is active (the Part 5 public/custom switch), so
 * the client can show it in the UI.
 */
@RestController
public class SourceController {

    private final OrderService orderService;

    public SourceController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/api/source")
    public Map<String, String> activeSource() {
        return Map.of("source", orderService.activeSourceName());
    }
}
