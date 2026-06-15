package com.example.iis.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

/**
 * Spring Web Services (Part 2). Registers the SOAP servlet at {@code /ws/*},
 * exposes a generated WSDL at {@code /ws/orders.wsdl} and binds it to the
 * {@code orders-soap.xsd} contract. Endpoint payloads are bound by JAXB through
 * the {@code @XmlRootElement} request/response classes (the default Spring-WS
 * JAXB method processors), so no explicit marshaller bean is required.
 */
@EnableWs
@Configuration
public class SoapConfig {

    public static final String NAMESPACE = "http://example.com/iis/orders-soap";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "orders")
    public DefaultWsdl11Definition ordersWsdl11Definition(XsdSchema ordersSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("OrdersPort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace(NAMESPACE);
        definition.setSchema(ordersSchema);
        return definition;
    }

    @Bean
    public XsdSchema ordersSchema() {
        return new SimpleXsdSchema(new ClassPathResource("schema/orders-soap.xsd"));
    }
}
