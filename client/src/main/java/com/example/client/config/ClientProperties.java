package com.example.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where the backend lives (REST/SOAP/GraphQL over HTTP, and the gRPC channel).
 */
@ConfigurationProperties(prefix = "backend")
public class ClientProperties {

    private String baseUrl = "http://localhost:8080";
    private final Grpc grpc = new Grpc();

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Grpc getGrpc() {
        return grpc;
    }

    public static class Grpc {
        private String host = "localhost";
        private int port = 9090;

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
