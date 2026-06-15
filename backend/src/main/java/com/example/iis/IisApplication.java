package com.example.iis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class IisApplication {

    public static void main(String[] args) {
        SpringApplication.run(IisApplication.class, args);
    }

}
