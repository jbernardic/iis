package com.example.iis.source;

import com.example.iis.domain.OrderEntity;
import com.example.iis.dto.OrderDto;
import com.example.iis.mapper.OrderMapper;
import com.example.iis.repo.OrderRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "app.order-source", havingValue = "custom", matchIfMissing = true)
public class DbOrderSource implements OrderSource {

    private final OrderRepository repository;

    public DbOrderSource(OrderRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return repository.findAll().stream().map(OrderMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDto> findById(Long id) {
        return repository.findById(id).map(OrderMapper::toDto);
    }

    @Override
    @Transactional
    public OrderDto create(OrderDto dto) {
        OrderEntity entity = OrderMapper.toNewEntity(dto);
        return OrderMapper.toDto(repository.save(entity));
    }

    @Override
    @Transactional
    public Optional<OrderDto> update(Long id, OrderDto dto) {
        return repository.findById(id).map(entity -> {
            OrderMapper.applyToEntity(dto, entity);
            return OrderMapper.toDto(repository.save(entity));
        });
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    public String name() {
        return "custom (H2 database)";
    }
}
