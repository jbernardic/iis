package com.example.iis.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;

/**
 * SOAP response payload (Part 2): the orders matching the search term.
 */
@XmlRootElement(name = "SearchOrdersResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"order", "matchCount"})
public class SearchOrdersResponse {

    @XmlElement(name = "order")
    private List<OrderInfo> order = new ArrayList<>();

    private int matchCount;

    public List<OrderInfo> getOrder() {
        return order;
    }

    public void setOrder(List<OrderInfo> order) {
        this.order = order;
    }

    public int getMatchCount() {
        return matchCount;
    }

    public void setMatchCount(int matchCount) {
        this.matchCount = matchCount;
    }
}
