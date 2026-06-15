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

/**
 * Builds the prepared {@code <orders>} XML file from the data returned by the
 * active order source (i.e. the same data a REST GET would return). The file is
 * the input for the SOAP/XPath search (Part 2) and the Jakarta XML validation
 * (Part 3).
 */
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

    /** Generates the prepared XML file from the current orders and returns its path. */
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

    /** Returns the prepared file content as a string (regenerating it first). */
    public String generateOrdersXml() {
        Path path = generateOrdersFile();
        try {
            return Files.readString(path);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to read generated orders XML", e);
        }
    }

    /**
     * Part 3: regenerates the prepared file and validates it against the XSD
     * using Jakarta XML validation; returns the validation messages (empty when
     * the file is valid).
     */
    public List<String> validateGeneratedFile() {
        Path path = generateOrdersFile();
        try {
            return xmlValidator.validate(path);
        } catch (Exception e) {
            return List.of("I/O error reading generated file: " + e.getMessage());
        }
    }
}
