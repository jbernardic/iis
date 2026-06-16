package com.example.iis.service;

import com.example.iis.config.AppProperties;
import com.example.iis.dto.OrderDto;
import com.example.iis.dto.OrdersDto;
import com.example.iis.validation.XmlSchemaValidator;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class OrderXmlService {

    private final OrderService orderService;
    private final AppProperties props;
    private final XmlSchemaValidator xmlValidator;
    private final JAXBContext jaxbContext;

    public OrderXmlService(OrderService orderService, AppProperties props,
                           XmlSchemaValidator xmlValidator) {
        this.orderService = orderService;
        this.props = props;
        this.xmlValidator = xmlValidator;
        try {
            this.jaxbContext = JAXBContext.newInstance(OrdersDto.class);
        } catch (JAXBException e) {
            throw new IllegalStateException("Cannot create JAXB context for OrdersDto", e);
        }
    }

    public synchronized Path generateOrdersFile() {
        List<OrderDto> orders = orderService.findAll();
        OrdersDto wrapper = new OrdersDto(orders);
        Path path = Path.of(props.getXml().getOrdersFile()).toAbsolutePath();
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            try (OutputStream os = Files.newOutputStream(path)) {
                marshaller.marshal(wrapper, os);
            }
            return path;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate orders XML file", e);
        }
    }

    public String generateOrdersXml() {
        Path path = generateOrdersFile();
        try {
            return Files.readString(path);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read generated orders XML", e);
        }
    }

    public List<String> validateGeneratedFile() {
        Path path = generateOrdersFile();
        try {
            return xmlValidator.validate(path);
        } catch (Exception e) {
            return List.of("I/O error reading generated file: " + e.getMessage());
        }
    }
}
