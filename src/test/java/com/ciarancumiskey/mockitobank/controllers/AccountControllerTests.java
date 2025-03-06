package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.TestConstants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTests {

    @InjectMocks AccountController accountMvc;
    @Mock AccountService accountService;

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccountsTest(final String sortCode, final String accountName, final String accountNumber, final String emailAddress, final String expectedIbanCode) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(any(String.class),any(String.class),any(String.class),any(String.class))).thenReturn(expectedAccount);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.createAccount(accountCreationReq);
        assertThat(createdAccountResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, accountName, emailAddress);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void getAccountsTest(final String sortCode, final String accountName, final String accountNumber, final String emailAddress, final String expectedIbanCode) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.findAccountByIban(any(String.class))).thenReturn(expectedAccount);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.getAccount(expectedIbanCode);
        assertThat(createdAccountResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, accountName, emailAddress);
    }

    private static Stream<Arguments> createAccountsParameters() {
        return Stream.of(
                TestConstants.USER_1_ARGS,
                TestConstants.USER_2_ARGS,
                TestConstants.USER_3_ARGS,
                TestConstants.USER_4_ARGS,
                TestConstants.USER_5_ARGS,
                TestConstants.USER_6_ARGS,
                TestConstants.USER_WO_EMAIL_ARGS
        );
    }

    private static void validateAccount(final ResponseEntity<Account> accountResponse,
                                        final String expectedIbanCode, final String accountNumber,
                                        final String sortCode, final String accountName, final String emailAddress){
        final Account createdAccount = accountResponse.getBody();
        assertNotNull(createdAccount);
        assertEquals(expectedIbanCode, createdAccount.getIbanCode());
        assertEquals(accountNumber, createdAccount.getAccountNumber());
        assertEquals(sortCode, createdAccount.getSortCode());
        assertEquals(accountName, createdAccount.getAccountName());
        assertEquals(emailAddress, createdAccount.getEmailAddress());
    }
}