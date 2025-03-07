package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.TestConstants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTests {

    @InjectMocks AccountController accountMvc;
    @Mock AccountService accountService;

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                            final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                            final String expectedIbanCode) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, expectedEmailAddress);
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(any(String.class),any(String.class),any(String.class),any(String.class)))
                .thenReturn(expectedAccount);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.createAccount(accountCreationReq);
        assertEquals(HttpStatus.CREATED, createdAccountResponse.getStatusCode());
        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, expectedAccountName,
                expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("createInvalidAccountParameters")
    void createAccountsTestErrors(final String sortCode, final String accountName, final String accountNumber,
                                  final String emailAddress, final String expectedIban) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.createAccount(accountCreationReq);
        assertEquals(HttpStatus.BAD_REQUEST, createdAccountResponse.getStatusCode());

        final Account accountFromDb = accountService.findAccountByIban(expectedIban);
        assertNull(accountFromDb);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void getAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                         final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                         final String expectedIbanCode) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.findAccountByIban(expectedIbanCode)).thenReturn(expectedAccount);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.getAccount(expectedIbanCode);
        assertThat(createdAccountResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, expectedAccountName,
                expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("updateAccountsParameters")
    void updateAccountsTest(final String originalIban, final String expectedUpdatedIban, final String existingSortCode,
                            final String newSortCode, final String existingAcNumber, final String newAcNumber,
                            final String existingName, final String newName, final String expectedNewName,
                            final String existingEmailAddress, final String newEmailAddress,
                            final String expectedNewEmailAddress){
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedOriginalAccount = new Account(existingSortCode, existingName, existingAcNumber, existingEmailAddress);
        expectedOriginalAccount.setIbanCode(originalIban);
        when(accountService.findAccountByIban(originalIban)).thenReturn(expectedOriginalAccount);

        final ResponseEntity<Account> createdAccountResponse = accountMvc.getAccount(originalIban);
        assertThat(createdAccountResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        validateAccount(createdAccountResponse, originalIban, existingAcNumber, existingSortCode, existingName,
                existingEmailAddress);

        final AccountUpdateRequest acUpdateRequest = new AccountUpdateRequest(originalIban, newSortCode, newAcNumber, newName, newEmailAddress);
        final ResponseEntity.HeadersBuilder<?> accountUpdateResponse = accountMvc.updateAccount(acUpdateRequest);

    }

    private static Stream<Arguments> createAccountsParameters() {
        return Stream.of(
                TestConstants.USER_1_ARGS,
                TestConstants.USER_2_ARGS,
                TestConstants.USER_3_ARGS,
                TestConstants.USER_4_ARGS,
                TestConstants.USER_5_ARGS,
                TestConstants.USER_6_ARGS,
                TestConstants.USER_WO_EMAIL_ARGS,
                TestConstants.USER_INVALID_EMAIL_ARGS,
                TestConstants.USER_TRAILING_WHITESPACE_1,
                TestConstants.USER_TRAILING_WHITESPACE_2,
                TestConstants.USER_TRAILING_WHITESPACE_3
        );
    }

    private static Stream<Arguments> createInvalidAccountParameters() {
        return Stream.of(
                TestConstants.USER_INVALID_SORT_CODE_5_NUMS,
                TestConstants.USER_INVALID_SORT_CODE_7_NUMS,
                TestConstants.USER_INVALID_AC_NUMBER_7_NUMS,
                TestConstants.USER_INVALID_AC_NUMBER_9_NUMS
        );
    }

    private static Stream<Arguments> updateAccountsParameters() {
        return Stream.of(
                Arguments.of(TestConstants.IBAN_1, TestConstants.UPDATED_IBAN_1, "123456", "234567", "12345678",
                        "12345678", "Joe Bloggs", "Joseph Bloggs ", "Joseph Bloggs", "jb@blahmail.com",
                        "jb@blahmail.com", "jb@blahmail.com")
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