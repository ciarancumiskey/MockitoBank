package com.ciarancumiskey.mockitobank.models;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {

    //TODO: Create mock DB

    @BeforeAll
    static void setUp() {

    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccounts(final String sortCode, final String accountName, final String accountNumber, final String expectedIbanCode) {
        Account newAccount = new Account(sortCode, accountName, accountNumber);
        assertEquals(accountName, newAccount.getAccountName());
        assertEquals(accountNumber, newAccount.getAccountNumber());
        assertEquals(sortCode, newAccount.getSortCode());
        assertEquals(expectedIbanCode, newAccount.getIbanCode());
        assertEquals(BigDecimal.ZERO, newAccount.getBalance());
        assertEquals(BigDecimal.ZERO, newAccount.getOverdraftLimit());
    }

    private static Stream<Arguments> createAccountsParameters() {
        return Stream.of(
                Arguments.of("123456", "Joe Bloggs", "12345678", "IE94MOCK12345612345678"),
                Arguments.of("654321", "Jacqui Bloggs", "87650987", "IE94MOCK65432187650987"),
                Arguments.of("065432", "James Bloggs", "08765098", "IE94MOCK06543208765098"),
                Arguments.of("012345", "Jane Bloggs", "12345678", "IE94MOCK01234512345678")
        );
    }

    @Test
    void deposit() {
    }

    @Test
    void withdraw() {
    }

    @Test
    void getAccountNumber() {
    }

    @Test
    void getSortCode() {
    }

    @Test
    void getIbanCode() {
    }

    @Test
    void getAccountName() {
    }

    @Test
    void getBalance() {
    }

    @Test
    void getOverdraftLimit() {
    }

    @Test
    void setAccountNumber() {
    }

    @Test
    void setSortCode() {
    }

    @Test
    void setIbanCode() {
    }

    @Test
    void setAccountName() {
    }

    @Test
    void setBalance() {
    }

    @Test
    void setOverdraftLimit() {
    }
}