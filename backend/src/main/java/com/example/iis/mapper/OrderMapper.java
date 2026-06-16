package com.example.iis.mapper;

import com.example.iis.domain.Billing;
import com.example.iis.domain.LineItemEntity;
import com.example.iis.domain.OrderEntity;
import com.example.iis.dto.BillingDto;
import com.example.iis.dto.LineItemDto;
import com.example.iis.dto.OrderDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class OrderMapper {

    private static final DateTimeFormatter OUT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private OrderMapper() {
    }

    public static OrderDto toDto(OrderEntity e) {
        OrderDto dto = new OrderDto();
        dto.setId(e.getId());
        dto.setNumber(e.getNumber());
        dto.setStatus(e.getStatus());
        dto.setCurrency(e.getCurrency());
        dto.setTotal(e.getTotal());
        dto.setCustomerNote(e.getCustomerNote());
        dto.setDateCreated(e.getDateCreated() != null ? e.getDateCreated().format(OUT) : null);

        Billing b = e.getBilling();
        if (b != null) {
            BillingDto bd = new BillingDto();
            bd.setFirstName(b.getFirstName());
            bd.setLastName(b.getLastName());
            bd.setEmail(b.getEmail());
            bd.setPhone(b.getPhone());
            bd.setCity(b.getCity());
            bd.setCountry(b.getCountry());
            dto.setBilling(bd);
        }

        List<LineItemDto> items = new ArrayList<>();
        for (LineItemEntity li : e.getLineItems()) {
            LineItemDto ld = new LineItemDto();
            ld.setProductId(li.getProductId());
            ld.setName(li.getName());
            ld.setQuantity(li.getQuantity());
            ld.setPrice(li.getPrice());
            ld.setSubtotal(li.getSubtotal());
            items.add(ld);
        }
        dto.setLineItems(items);
        return dto;
    }

    public static OrderEntity toNewEntity(OrderDto dto) {
        OrderEntity e = new OrderEntity();
        applyScalars(dto, e);
        return e;
    }

    public static void applyToEntity(OrderDto dto, OrderEntity e) {
        applyScalars(dto, e);
    }

    private static void applyScalars(OrderDto dto, OrderEntity e) {
        e.setNumber(dto.getNumber());
        e.setStatus(dto.getStatus());
        e.setCurrency(dto.getCurrency());
        e.setTotal(dto.getTotal());
        e.setCustomerNote(dto.getCustomerNote());
        e.setDateCreated(parseDate(dto.getDateCreated()));

        Billing b = e.getBilling() != null ? e.getBilling() : new Billing();
        BillingDto bd = dto.getBilling();
        if (bd != null) {
            b.setFirstName(bd.getFirstName());
            b.setLastName(bd.getLastName());
            b.setEmail(bd.getEmail());
            b.setPhone(bd.getPhone());
            b.setCity(bd.getCity());
            b.setCountry(bd.getCountry());
        }
        e.setBilling(b);

        e.getLineItems().clear();
        if (dto.getLineItems() != null) {
            for (LineItemDto ld : dto.getLineItems()) {
                LineItemEntity li = new LineItemEntity();
                li.setProductId(ld.getProductId());
                li.setName(ld.getName());
                li.setQuantity(ld.getQuantity());
                li.setPrice(ld.getPrice());
                li.setSubtotal(ld.getSubtotal());
                e.addLineItem(li);
            }
        }
    }

    private static LocalDateTime parseDate(String value) {
        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value);
        } catch (Exception ex) {
            return LocalDateTime.parse(value + "T00:00:00");
        }
    }
}
