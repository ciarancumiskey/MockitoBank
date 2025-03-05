package com.ciarancumiskey.mockitobank;

import org.springframework.boot.SpringApplication;

public class TestMockitoBankApplication {

    public static void main(String[] args) {
        SpringApplication.from(MockitoBankApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
