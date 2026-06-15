package com.example.iis.service;

import com.example.iis.dto.OrderDto;
import com.example.iis.source.OrderSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Thin facade the REST controller and GraphQL controller use. It always talks
 * to whichever {@link OrderSource} the {@code app.order-source} switch selected,
 * so the public/custom toggle is transparent to callers.
 */
@Service
public class OrderService {

    private final OrderSource source;

    public OrderService(OrderSource source) {
        this.source = source;
    }

    public List<OrderDto> findAll() {
        return source.findAll();
    }

    public Optional<OrderDto> findById(Long id) {
        return source.findById(id);
    }

    public OrderDto create(OrderDto dto) {
        return source.create(dto);
    }

    public Optional<OrderDto> update(Long id, OrderDto dto) {
        return source.update(id, dto);
    }

    public boolean delete(Long id) {
        return source.delete(id);
    }

    public String activeSourceName() {
        return source.name();
    }
}
