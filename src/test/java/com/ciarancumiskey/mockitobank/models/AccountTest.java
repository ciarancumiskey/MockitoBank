package com.ciarancumiskey.mockitobank.models;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AccountTest {

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccounts(final String sortCode, final String accountName, final String accountNumber, final String emailAddress, final String expectedIbanCode) {
        log.info("Creating account for {}", accountName);
        Account newAccount = new Account("IE94MOCK", sortCode, accountName, accountNumber, emailAddress);
        assertEquals(accountName, newAccount.getAccountName());
        assertEquals(accountNumber, newAccount.getAccountNumber());
        assertEquals(sortCode, newAccount.getSortCode());
        assertEquals(emailAddress, newAccount.getEmailAddress());
        assertEquals(expectedIbanCode, newAccount.getIbanCode());
        assertEquals(BigDecimal.ZERO, newAccount.getBalance());
        assertEquals(BigDecimal.ZERO, newAccount.getOverdraftLimit());
    }

    private static Stream<Arguments> createAccountsParameters() {
        return Stream.of(
                Arguments.of("123456", "Joe Bloggs", "12345678", "jb@blahmail.com", "IE94MOCK12345612345678"),
                Arguments.of("654321", "Jacqui Bloggs", "87650987", "jacqui.b@lorumipsum.com", "IE94MOCK65432187650987"),
                Arguments.of("065432", "James Bloggs", "08765098", "jamieb99@email.com", "IE94MOCK06543208765098"),
                Arguments.of("012345", "Jane Bloggs", "12345678", "janebloggs@lorummail.com", "IE94MOCK01234512345678"),
                Arguments.of("012345", "Anne-Marie Bloggs", "12345670", "am.bloggs@ipsummail.fr", "IE94MOCK01234512345670"),
                Arguments.of("656767", "Jean-Paul Bloggs", "02345678", "jp.bloggs@blahpost.ca","IE94MOCK65676702345678")
        );
    }
}