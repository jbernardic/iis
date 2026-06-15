package com.example.iis.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Canonical order representation used for:
 * <ul>
 *   <li>XML import / generation via JAXB ({@code <order>} root element),</li>
 *   <li>JSON import via Jackson,</li>
 *   <li>the custom REST API and GraphQL responses.</li>
 * </ul>
 * The same field names are described by {@code order.xsd} and
 * {@code order-schema.json} so a single document validates against both.
 */
@XmlRootElement(name = "order")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "number", "status", "currency", "total",
        "customerNote", "dateCreated", "billing", "lineItems"})
public class OrderDto {

    private Long id;
    private String number;
    private String status;
    private String currency;
    private BigDecimal total;
    private String customerNote;
    private String dateCreated;
    private BillingDto billing;

    @XmlElementWrapper(name = "lineItems")
    @XmlElement(name = "lineItem")
    private List<LineItemDto> lineItems = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public BillingDto getBilling() {
        return billing;
    }

    public void setBilling(BillingDto billing) {
        this.billing = billing;
    }

    public List<LineItemDto> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItemDto> lineItems) {
        this.lineItems = lineItems;
    }
}
