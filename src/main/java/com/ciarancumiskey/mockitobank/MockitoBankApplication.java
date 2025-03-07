package com.ciarancumiskey.mockitobank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MockitoBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockitoBankApplication.class, args);
    }

}
