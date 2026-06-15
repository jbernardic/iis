package com.example.client.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Client-side order model used for display, REST (de)serialization and form binding. */
public class OrderView {

    private Long id;
    private String number;
    private String status;
    private String currency;
    private BigDecimal total;
    private String customerNote;
    private String dateCreated;
    private BillingView billing = new BillingView();
    private List<LineItemView> lineItems = new ArrayList<>();

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

    public BillingView getBilling() {
        return billing;
    }

    public void setBilling(BillingView billing) {
        this.billing = billing;
    }

    public List<LineItemView> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItemView> lineItems) {
        this.lineItems = lineItems;
    }
}
