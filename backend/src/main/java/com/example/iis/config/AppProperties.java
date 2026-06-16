package com.example.iis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String orderSource = "custom";

    private final Jwt jwt = new Jwt();
    private final Grpc grpc = new Grpc();
    private final Dhmz dhmz = new Dhmz();
    private final Xml xml = new Xml();
    private final WooCommerce woocommerce = new WooCommerce();

    public String getOrderSource() {
        return orderSource;
    }

    public void setOrderSource(String orderSource) {
        this.orderSource = orderSource;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public Grpc getGrpc() {
        return grpc;
    }

    public Dhmz getDhmz() {
        return dhmz;
    }

    public Xml getXml() {
        return xml;
    }

    public WooCommerce getWoocommerce() {
        return woocommerce;
    }

    public static class Jwt {
        private String secret;
        private int accessTokenTtlMinutes = 15;
        private int refreshTokenTtlDays = 7;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public int getAccessTokenTtlMinutes() {
            return accessTokenTtlMinutes;
        }

        public void setAccessTokenTtlMinutes(int accessTokenTtlMinutes) {
            this.accessTokenTtlMinutes = accessTokenTtlMinutes;
        }

        public int getRefreshTokenTtlDays() {
            return refreshTokenTtlDays;
        }

        public void setRefreshTokenTtlDays(int refreshTokenTtlDays) {
            this.refreshTokenTtlDays = refreshTokenTtlDays;
        }
    }

    public static class Grpc {
        private int port = 9090;

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

    public static class Dhmz {
        private String url = "https://vrijeme.hr/hrvatska_n.xml";

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Xml {
        private String ordersFile = "data/orders-data.xml";

        public String getOrdersFile() {
            return ordersFile;
        }

        public void setOrdersFile(String ordersFile) {
            this.ordersFile = ordersFile;
        }
    }

    public static class WooCommerce {
        private String baseUrl;
        private String consumerKey;
        private String consumerSecret;

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getConsumerKey() {
            return consumerKey;
        }

        public void setConsumerKey(String consumerKey) {
            this.consumerKey = consumerKey;
        }

        public String getConsumerSecret() {
            return consumerSecret;
        }

        public void setConsumerSecret(String consumerSecret) {
            this.consumerSecret = consumerSecret;
        }
    }
}
