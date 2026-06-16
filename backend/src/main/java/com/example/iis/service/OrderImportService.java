package com.example.iis.service;

import com.example.iis.domain.OrderEntity;
import com.example.iis.dto.FileImportResult;
import com.example.iis.dto.OrderDto;
import com.example.iis.mapper.OrderMapper;
import com.example.iis.repo.OrderRepository;
import com.example.iis.validation.JsonSchemaValidator;
import com.example.iis.validation.XmlSchemaValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.util.List;

@Service
public class OrderImportService {

    private final XmlSchemaValidator xmlValidator;
    private final JsonSchemaValidator jsonValidator;
    private final OrderRepository repository;
    private final ObjectMapper mapper;
    private final JAXBContext jaxbContext;

    public OrderImportService(XmlSchemaValidator xmlValidator,
                              JsonSchemaValidator jsonValidator,
                              OrderRepository repository,
                              ObjectMapper mapper) {
        this.xmlValidator = xmlValidator;
        this.jsonValidator = jsonValidator;
        this.repository = repository;
        this.mapper = mapper;
        try {
            this.jaxbContext = JAXBContext.newInstance(OrderDto.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Cannot create JAXB context for OrderDto", e);
        }
    }

    @Transactional
    public FileImportResult importXml(byte[] xml) {
        List<String> errors = xmlValidator.validate(xml);
        if (!errors.isEmpty()) {
            return new FileImportResult("xml", false, errors, null);
        }
        try {
            OrderDto dto = (OrderDto) jaxbContext.createUnmarshaller()
                    .unmarshal(new StreamSource(new ByteArrayInputStream(xml)));
            OrderEntity saved = repository.save(OrderMapper.toNewEntity(dto));
            return new FileImportResult("xml", true, List.of(), saved.getId());
        } catch (Exception e) {
            return new FileImportResult("xml", false,
                    List.of("Could not parse/persist a valid-looking document: " + e.getMessage()), null);
        }
    }

    @Transactional
    public FileImportResult importJson(byte[] json) {
        List<String> errors = jsonValidator.validate(json);
        if (!errors.isEmpty()) {
            return new FileImportResult("json", false, errors, null);
        }
        try {
            OrderDto dto = mapper.readValue(json, OrderDto.class);
            OrderEntity saved = repository.save(OrderMapper.toNewEntity(dto));
            return new FileImportResult("json", true, List.of(), saved.getId());
        } catch (Exception e) {
            return new FileImportResult("json", false,
                    List.of("Could not parse/persist a valid-looking document: " + e.getMessage()), null);
        }
    }
}
