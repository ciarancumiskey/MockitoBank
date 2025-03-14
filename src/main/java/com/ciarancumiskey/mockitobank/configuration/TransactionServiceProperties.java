package com.ciarancumiskey.mockitobank.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "transaction-service")
public class TransactionServiceProperties {
    // stub class to prevent UnsatisfiedDependencyException
}
