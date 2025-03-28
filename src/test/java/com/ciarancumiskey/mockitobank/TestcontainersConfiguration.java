package com.ciarancumiskey.mockitobank;

import com.ciarancumiskey.mockitobank.utils.TestConstants;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>(TestConstants.MYSQL_80_IMAGE_NAME);
    }

    @Bean
    TestRestTemplate restTemplate(){
        return new TestRestTemplate();
    }
}
