package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.models.Account;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {

    //TODO: Create mock DB
    @Mock
    AccountDbRepository mockAccountDb;
    //TODO: Create mock service
    @InjectMocks
    AccountService mockAccountService;

    @BeforeAll
    static void setUp() {

    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccounts(final String sortCode, final String accountName, final String accountNumber, final String emailAddress, final String expectedIbanCode) {
        log.info("Creating account for {}", accountName);

        // Create the Account object to test with
        final Account createdAccount = new Account(sortCode, accountName, accountNumber, emailAddress);

        // Mock the repository's findById method to return the account for the given IBAN code
        when(mockAccountDb.findById(expectedIbanCode)).thenReturn(Optional.of(createdAccount));

        // Call the service method that should save the account
        Account account = mockAccountService.createAccount(sortCode, accountName, accountNumber, emailAddress);

        // Assert that the account is correctly created
        assertNotNull(account);
        assertEquals(expectedIbanCode, account.getIbanCode());

        // Verify that the save method was called on the repository
        verify(mockAccountDb, times(1)).save(account);
        // Assert: Verify that the account is not null when retrieved via findById
        final Account foundAccount = mockAccountDb.findById(expectedIbanCode).orElseThrow();
        assertNotNull(foundAccount);

        // Additional check for the values if necessary
        assertEquals(expectedIbanCode, foundAccount.getIbanCode());
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
