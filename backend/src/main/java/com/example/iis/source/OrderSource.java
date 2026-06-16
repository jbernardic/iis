package com.example.iis.source;

import com.example.iis.dto.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderSource {

    List<OrderDto> findAll();

    Optional<OrderDto> findById(Long id);

    OrderDto create(OrderDto dto);

    Optional<OrderDto> update(Long id, OrderDto dto);

    boolean delete(Long id);

    String name();
}
