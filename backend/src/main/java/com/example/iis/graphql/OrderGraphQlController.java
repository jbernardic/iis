package com.example.iis.graphql;

import com.example.iis.dto.OrderDto;
import com.example.iis.service.OrderService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * Part 5 GraphQL API over the same {@link OrderService}. Queries are available
 * to any authenticated user; mutations require the full-access role
 * ({@code @PreAuthorize}), matching the REST authorization rules.
 */
@Controller
public class OrderGraphQlController {

    private final OrderService orderService;

    public OrderGraphQlController(OrderService orderService) {
        this.orderService = orderService;
    }

    @QueryMapping
    public List<OrderDto> orders() {
        return orderService.findAll();
    }

    @QueryMapping
    public OrderDto order(@Argument Long id) {
        return orderService.findById(id).orElse(null);
    }

    @QueryMapping
    public String activeSource() {
        return orderService.activeSourceName();
    }

    @MutationMapping
    @PreAuthorize("hasRole('FULL')")
    public OrderDto createOrder(@Argument OrderDto input) {
        return orderService.create(input);
    }

    @MutationMapping
    @PreAuthorize("hasRole('FULL')")
    public OrderDto updateOrder(@Argument Long id, @Argument OrderDto input) {
        return orderService.update(id, input).orElse(null);
    }

    @MutationMapping
    @PreAuthorize("hasRole('FULL')")
    public boolean deleteOrder(@Argument Long id) {
        return orderService.delete(id);
    }
}
