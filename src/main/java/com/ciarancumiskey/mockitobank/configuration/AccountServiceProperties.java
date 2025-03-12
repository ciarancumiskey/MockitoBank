package com.ciarancumiskey.mockitobank.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "account-service")
public class AccountServiceProperties {
    private String bankIdentifierCode;
}
