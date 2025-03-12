package com.ciarancumiskey.mockitobank;

import com.ciarancumiskey.mockitobank.configuration.AccountServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableConfigurationProperties(AccountServiceProperties.class)
public class MockitoBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockitoBankApplication.class, args);
    }

}
