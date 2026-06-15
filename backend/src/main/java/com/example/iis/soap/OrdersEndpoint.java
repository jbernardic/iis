package com.example.iis.soap;

import com.example.iis.config.SoapConfig;
import com.example.iis.service.OrderSearchService;
import com.example.iis.service.OrderXmlService;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.nio.file.Path;
import java.util.List;

/**
 * Part 2 SOAP endpoint. For each request it (1) regenerates the prepared XML
 * file from current order data, then (2) filters it with XPath by the supplied
 * term and returns the matches.
 */
@Endpoint
public class OrdersEndpoint {

    private final OrderXmlService orderXmlService;
    private final OrderSearchService orderSearchService;

    public OrdersEndpoint(OrderXmlService orderXmlService, OrderSearchService orderSearchService) {
        this.orderXmlService = orderXmlService;
        this.orderSearchService = orderSearchService;
    }

    @PayloadRoot(namespace = SoapConfig.NAMESPACE, localPart = "SearchOrdersRequest")
    @ResponsePayload
    public SearchOrdersResponse searchOrders(@RequestPayload SearchOrdersRequest request) throws Exception {
        Path preparedFile = orderXmlService.generateOrdersFile();
        List<OrderInfo> matches = orderSearchService.search(preparedFile, request.getTerm());

        SearchOrdersResponse response = new SearchOrdersResponse();
        response.setOrder(matches);
        response.setMatchCount(matches.size());
        return response;
    }
}
