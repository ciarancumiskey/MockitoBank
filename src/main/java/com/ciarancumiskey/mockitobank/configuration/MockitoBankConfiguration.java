package com.ciarancumiskey.mockitobank.configuration;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.services.AccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockitoBankConfiguration {

    @Bean
    public AccountService accountService(final AccountDbRepository accountDbRepository,
                                         final AccountServiceProperties accountServiceProperties){
        return new AccountService(accountDbRepository, accountServiceProperties.getBankIdentifierCode());
    }
}
