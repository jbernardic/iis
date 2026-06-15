package com.example.iis.source;

import com.example.iis.dto.OrderDto;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction over the place orders live. Two implementations exist and are
 * selected by the {@code app.order-source} switch (Part 5):
 * <ul>
 *   <li>{@link DbOrderSource} — the application's own H2 database ("custom"),</li>
 *   <li>{@link WooCommerceOrderSource} — the public WooCommerce REST API.</li>
 * </ul>
 */
public interface OrderSource {

    List<OrderDto> findAll();

    Optional<OrderDto> findById(Long id);

    OrderDto create(OrderDto dto);

    Optional<OrderDto> update(Long id, OrderDto dto);

    boolean delete(Long id);

    /** Human-readable id of the active source, surfaced to clients. */
    String name();
}
