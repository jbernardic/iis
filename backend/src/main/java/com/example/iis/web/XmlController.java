package com.example.iis.web;

import com.example.iis.service.OrderXmlService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Part 3 REST interface. Exposes the prepared {@code <orders>} XML file and the
 * result of validating it against the XSD with Jakarta XML validation.
 */
@RestController
@RequestMapping("/api/xml")
public class XmlController {

    private final OrderXmlService orderXmlService;

    public XmlController(OrderXmlService orderXmlService) {
        this.orderXmlService = orderXmlService;
    }

    /** Returns the prepared XML (regenerated from current order data). */
    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_XML_VALUE)
    public String generatedOrders() {
        return orderXmlService.generateOrdersXml();
    }

    /** Regenerates and validates the prepared file; reports any problems. */
    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidationReport validate() {
        List<String> messages = orderXmlService.validateGeneratedFile();
        return new ValidationReport(messages.isEmpty(), messages);
    }

    public record ValidationReport(boolean valid, List<String> messages) {
    }
}
