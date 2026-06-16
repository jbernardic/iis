package com.example.iis.web;

import com.example.iis.service.OrderXmlService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/xml")
public class XmlController {

    private final OrderXmlService orderXmlService;

    public XmlController(OrderXmlService orderXmlService) {
        this.orderXmlService = orderXmlService;
    }

    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_XML_VALUE)
    public String generatedOrders() {
        return orderXmlService.generateOrdersXml();
    }

    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationReport validate() {
        List<String> messages = orderXmlService.validateGeneratedFile();
        return new ValidationReport(messages.isEmpty(), messages);
    }

    public record ValidationReport(boolean valid, List<String> messages) {
    }
}
