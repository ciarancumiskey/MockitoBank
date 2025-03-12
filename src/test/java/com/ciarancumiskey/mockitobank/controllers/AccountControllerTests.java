package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.exceptions.AlreadyExistsException;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import com.ciarancumiskey.mockitobank.utils.TestConstants;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.utils.Constants.*;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.*;
import static com.fasterxml.jackson.databind.type.LogicalType.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @Autowired private MockMvc accountMockMvc;
    @MockitoBean AccountService accountService;

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                            final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                            final String expectedIbanCode) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, expectedEmailAddress);
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(any(String.class),any(String.class),any(String.class),any(String.class)))
                .thenReturn(expectedAccount);

        MvcResult createAcMvcResult = accountMockMvc.perform(MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(accountCreationReq)))
                .andExpect(status().isOk()).andReturn();
        validateAccount(createAcMvcResult.getResponse().getContentAsString(), expectedIbanCode, accountNumber, sortCode, expectedAccountName, expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createDuplicateAccountsTest(final String sortCode, final String duplicateAccountName, final String expectedAccountName,
                            final String accountNumber, final String duplicateEmailAddress, final String expectedEmailAddress,
                            final String expectedIbanCode) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final String originalName = "O. G. Test";
        final String originalEmail = "og@testmail.com";
        final Account existingAccount = new Account(sortCode, originalName, accountNumber, originalEmail);
        existingAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(sortCode, originalName, accountNumber, originalEmail)).thenReturn(existingAccount);
        final String expectedErrorMessage = ERROR_MSG_DUPLICATE_IBAN.formatted(expectedIbanCode);
        when(accountService.createAccount(sortCode, duplicateAccountName, accountNumber, duplicateEmailAddress)).thenThrow(new AlreadyExistsException(expectedErrorMessage));

        // Try sending another request to create an account with the same sort code and account number
        final AccountCreationRequest duplicateAccountCreationReq = new AccountCreationRequest(sortCode, duplicateAccountName,
                accountNumber, duplicateEmailAddress);
        MvcResult mvcResult = accountMockMvc.perform(
                        MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                                .content(TestUtils.asJsonString(duplicateAccountCreationReq))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict()).andReturn();
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(expectedErrorMessage));
    }

    @ParameterizedTest
    @MethodSource("createInvalidAccountParameters")
    void createAccountsTestErrors(final String sortCode, final String accountName, final String accountNumber,
                                  final String emailAddress, final String expectedErrorMessage) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(accountService.createAccount(sortCode, accountName, accountNumber, emailAddress))
                .thenThrow(new InvalidArgumentsException(expectedErrorMessage));
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);

        MvcResult mvcResult = accountMockMvc.perform(MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                        .content(TestUtils.asJsonString(accountCreationReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(expectedErrorMessage), "Error message was actually " + errorResponseString);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void getAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                         final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                         final String expectedIbanCode) throws Exception {
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.findAccountByIban(expectedIbanCode)).thenReturn(expectedAccount);

        MvcResult accountGetMvcResult = accountMockMvc.perform(MockMvcRequestBuilders.get(Constants.ACCOUNT_PATH + Constants.LOAD_ACCOUNT_PATH.replace("{accountIban}", expectedIbanCode))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();
        validateAccount(accountGetMvcResult.getResponse().getContentAsString(), expectedIbanCode, accountNumber, sortCode, expectedAccountName, expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("updateAccountsParameters")
    void updateAccountsTest(final String originalIban, final String existingSortCode, final String existingAcNumber,
                            final String existingName, final String newName, final String expectedNewName,
                            final String existingEmailAddress, final String newEmailAddress,
                            final String expectedNewEmailAddress) throws Exception {
        final Account expectedOriginalAccount = new Account(existingSortCode, existingName, existingAcNumber, existingEmailAddress);
        expectedOriginalAccount.setIbanCode(originalIban);
        when(accountService.findAccountByIban(originalIban)).thenReturn(expectedOriginalAccount);

        final AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(originalIban, newName, newEmailAddress);
        String jsonRequest = TestUtils.asJsonString(accountUpdateRequest);
        log.info("Serialized JSON Request: {}", jsonRequest);

        when(accountService.updateAccount(any(AccountUpdateRequest.class)))
                .thenReturn(new Account(existingSortCode, newName, existingAcNumber, expectedNewEmailAddress));

        final MvcResult accountUpdateResult = accountMockMvc.perform(MockMvcRequestBuilders.put(Constants.ACCOUNT_PATH + Constants.UPDATE_ACCOUNT_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(accountUpdateResult);
        assertNotNull(accountUpdateResult.getResponse());
        final String responseContent = accountUpdateResult.getResponse().getContentAsString();
        validateAccount(responseContent, originalIban, existingAcNumber, existingSortCode, expectedNewName, expectedNewEmailAddress);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "\t\t", "\n", "      ", "123", "123456767890", TestConstants.IBAN_FAIL_1,
            TestConstants.IBAN_FAIL_2, TestConstants.IBAN_FAIL_3, TestConstants.IBAN_FAIL_4, TestConstants.IBAN_FAIL_5,
            TestConstants.IBAN_FAIL_6, TestConstants.IBAN_FAIL_7})
    void updateAccountsInvalidIbanTest(final String invalidIban)
            throws Exception {
        final String existingName = "Test Account";
        final String existingEmail = "test.account@zinkworks.com";

        when(accountService.updateAccount(any(AccountUpdateRequest.class))).thenThrow(new InvalidArgumentsException(ERROR_MSG_INVALID_IBAN));

        final AccountUpdateRequest invalidUpdateRequest = new AccountUpdateRequest(invalidIban, existingName, existingEmail);
        String jsonRequest = TestUtils.asJsonString(invalidUpdateRequest);
        log.info("Serialized JSON Request: {}", jsonRequest);

        MvcResult mvcResult = accountMockMvc.perform(MockMvcRequestBuilders.put(Constants.ACCOUNT_PATH + Constants.UPDATE_ACCOUNT_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()).andReturn();
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        HashMap errorResponseContent = g.fromJson(errorResponseString, HashMap.class);
        assertNotNull(errorResponseContent.get("message"));
        log.info("Error content: {}", errorResponseContent.get("message"));
    }

    @ParameterizedTest
    @ValueSource(strings = {TestConstants.UPDATED_IBAN_1, TestConstants.IBAN_2, TestConstants.IBAN_3,
            TestConstants.IBAN_4, TestConstants.IBAN_5, TestConstants.IBAN_6, TestConstants.IBAN_WO_EMAIL,
            TestConstants.IBAN_INVALID_EMAIL, TestConstants.IBAN_WHITESPACE_1, TestConstants.IBAN_WHITESPACE_2,
            TestConstants.IBAN_WHITESPACE_3})
    void updateAccountsIncorrectIbanTest(final String incorrectIban)
            throws Exception {
        final String existingName = "Test Account";
        final String existingEmail = "test.account@zinkworks.com";

        final AccountUpdateRequest invalidUpdateRequest = new AccountUpdateRequest(incorrectIban, existingName, existingEmail);
        // Use any() matcher if the exact object equality is not guaranteed
        final String expectedErrorMessage = "No account found with IBAN " + incorrectIban;
        when(accountService.updateAccount(any(AccountUpdateRequest.class))).thenThrow(new NotFoundException(expectedErrorMessage));

        String jsonRequest = TestUtils.asJsonString(invalidUpdateRequest);
        log.info("Serialized JSON Request: {}", jsonRequest);

        MvcResult mvcResult = accountMockMvc.perform(MockMvcRequestBuilders.put(Constants.ACCOUNT_PATH + Constants.UPDATE_ACCOUNT_PATH)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andReturn();
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage), "Error message was actually " + errorResponseString);
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
                TestConstants.USER_INVALID_AC_NUMBER_9_NUMS,
                TestConstants.USER_INVALID_NO_NAME,
                TestConstants.USER_INVALID_WHITESPACE_NAME,
                TestConstants.USER_INVALID_TABS_NAME,
                TestConstants.USER_INVALID_NEWLINE_NAME
        );
    }

    private static Stream<Arguments> updateAccountsParameters() {
        return Stream.of(
                IBAN_1_JOSEPH,
                IBAN_1_JBLOGGS_EMAIL,
                // Test that whitespaces are being trimmed from updated email addresses and customer names
                IBAN_1_EMAIL_WHITESPACE,
                IBAN_1_EMAIL_TABS,
                IBAN_1_EMAIL_NEWLINES,
                IBAN_WHITESPACE_1_NEW_EMAIL
        );
    }

    private static void validateAccount(final String accountJsonString,
                                        final String expectedIbanCode, final String expectedAcNumber,
                                        final String expectedSortCode, final String expectedAcName, final String expectedEmailAddress){
        final Account parsedAccount = (Account) TestUtils.fromJsonString(accountJsonString, Account.class);
        log.info("Validating account: {}", parsedAccount);
        assertEquals(expectedIbanCode, parsedAccount.getIbanCode());
        assertEquals(expectedAcNumber, parsedAccount.getAccountNumber());
        assertEquals(expectedSortCode, parsedAccount.getSortCode());
        assertEquals(expectedAcName, parsedAccount.getAccountName());
        assertEquals(expectedEmailAddress, parsedAccount.getEmailAddress());
    }
}