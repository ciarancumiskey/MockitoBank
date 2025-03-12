package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.configuration.AccountServiceProperties;
import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.utils.Constants.*;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@WebMvcTest(AccountController.class)
public class AccountControllerTests {

    @Autowired
    private MockMvc accountMockMvc;

    @MockitoBean
    private AccountService accountService;

    @Mock
    private AccountServiceProperties accountServiceProperties;

    @InjectMocks
    private AccountService accountServiceWithMocks;

    @BeforeEach
    void setUp() {
        // Set up the IBAN prefix for the test
        when(accountServiceProperties.getBankIdentifierCode()).thenReturn(TEST_BIC);
    }


    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                            final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                            final String expectedIbanCode) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedAccount = new Account(accountServiceProperties.getBankIdentifierCode(), sortCode, accountName, accountNumber, expectedEmailAddress);
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(any(String.class),any(String.class),any(String.class),any(String.class)))
                .thenReturn(expectedAccount);

        MvcResult createAcMvcResult = TestUtils.sendPostRequest(accountMockMvc, ACCOUNT_PATH + REGISTRATION_PATH,
                TestUtils.asJsonString(accountCreationReq), status().isOk());
        validateAccount(createAcMvcResult.getResponse().getContentAsString(), expectedIbanCode, accountNumber, sortCode, expectedAccountName, expectedEmailAddress);
        // Verify the amount of times createAccount() was called
        verify(accountService, times(1)).createAccount(sortCode, accountName, accountNumber, emailAddress);
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
        final Account existingAccount = new Account(accountServiceProperties.getBankIdentifierCode(), sortCode, originalName, accountNumber, originalEmail);
        existingAccount.setIbanCode(expectedIbanCode);
        when(accountService.createAccount(sortCode, originalName, accountNumber, originalEmail)).thenReturn(existingAccount);
        final String expectedErrorMessage = ERROR_MSG_DUPLICATE_IBAN.formatted(expectedIbanCode);
        when(accountService.createAccount(sortCode, duplicateAccountName, accountNumber, duplicateEmailAddress)).thenThrow(new AlreadyExistsException(expectedErrorMessage));

        // Try sending another request to create an account with the same sort code and account number
        final AccountCreationRequest duplicateAccountCreationReq = new AccountCreationRequest(sortCode, duplicateAccountName,
                accountNumber, duplicateEmailAddress);
        MvcResult mvcResult = TestUtils.sendPostRequest(accountMockMvc,
                        Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH,
                                TestUtils.asJsonString(duplicateAccountCreationReq), status().isConflict());
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(expectedErrorMessage));
        // Verify the amount of times createAccount() was called
        verify(accountService, times(1)).createAccount(sortCode, duplicateAccountName, accountNumber,
                duplicateEmailAddress);
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

        MvcResult mvcResult = TestUtils.sendPostRequest(accountMockMvc,
                Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH,
                TestUtils.asJsonString(accountCreationReq), status().isBadRequest());
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(expectedErrorMessage), "Error message was actually " + errorResponseString);

        // Verify the amount of times createAccount() was called
        verify(accountService, times(1)).createAccount(sortCode, accountName, accountNumber, emailAddress);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void getAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                         final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                         final String expectedIbanCode) throws Exception {
        final Account expectedAccount = new Account(accountServiceProperties.getBankIdentifierCode(), sortCode,
                accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.findAccountByIban(expectedIbanCode)).thenReturn(expectedAccount);

        final String getAccountPath = Constants.ACCOUNT_PATH + Constants.LOAD_ACCOUNT_PATH.replace("{accountIban}", expectedIbanCode);
        final MvcResult accountGetMvcResult = TestUtils.sendGetRequest(accountMockMvc, getAccountPath, status().isOk());
        validateAccount(accountGetMvcResult.getResponse().getContentAsString(), expectedIbanCode, accountNumber,
                sortCode, expectedAccountName, expectedEmailAddress);
        // Verify the amount of times findAccountByIban() was called
        verify(accountService, times(1)).findAccountByIban(expectedIbanCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"IE93MOCK12345612345678", "IE93MOCK22345612345678", "IE94M0CK22345612345678",
            "IE94ZINK22345612345678", "IE12DNPQ12345612345678"})
    void getNonExistantAccountTest(final String expectedIbanCode) throws Exception {
        final String expectedNotFoundErrorMessage = ERROR_MSG_IBAN_NOT_FOUND.formatted(expectedIbanCode);
        when(accountService.findAccountByIban(expectedIbanCode)).thenThrow(new NotFoundException(expectedNotFoundErrorMessage));

        final String getAccountPath = Constants.ACCOUNT_PATH + Constants.LOAD_ACCOUNT_PATH.replace("{accountIban}", expectedIbanCode);
        final MvcResult accountGetMvcResult = TestUtils.sendGetRequest(accountMockMvc, getAccountPath, status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedNotFoundErrorMessage), "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(accountService, times(1)).findAccountByIban(expectedIbanCode);
    }

    @ParameterizedTest
    @ValueSource(strings = {"IE93MOCK1234561234567", "IE93MOCK123456123456789", "IE93MOCK1234512345678",
            "IE93MOCK123456712345678", "IE93M0K22345612345678", "IE94M0CK2234561235678", "E94ZINK223456123456787",
            "E12DNPQ12345612345678"})
    void getAccountWithInvalidIbanTest(final String invalidIban) throws Exception {
        final String expectedNotFoundErrorMessage = ERROR_MSG_INVALID_IBAN;
        when(accountService.findAccountByIban(invalidIban)).thenThrow(new InvalidArgumentsException(expectedNotFoundErrorMessage));

        final String getAccountPath = Constants.ACCOUNT_PATH + Constants.LOAD_ACCOUNT_PATH.replace("{accountIban}", invalidIban);
        final MvcResult accountGetMvcResult = TestUtils.sendGetRequest(accountMockMvc, getAccountPath, status().isBadRequest());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedNotFoundErrorMessage), "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(accountService, times(1)).findAccountByIban(invalidIban);
    }

    @ParameterizedTest
    @MethodSource("updateAccountsParameters")
    void updateAccountsTest(final String originalIban, final String existingSortCode, final String existingAcNumber,
                            final String existingName, final String newName, final String expectedNewName,
                            final String existingEmailAddress, final String newEmailAddress,
                            final String expectedNewEmailAddress) throws Exception {
        when(accountServiceProperties.getBankIdentifierCode()).thenReturn(TEST_BIC);

        final Account expectedOriginalAccount = new Account(accountServiceProperties.getBankIdentifierCode(),
                existingSortCode, existingName, existingAcNumber, existingEmailAddress);
        expectedOriginalAccount.setIbanCode(originalIban);
        when(accountService.findAccountByIban(originalIban)).thenReturn(expectedOriginalAccount);

        final AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(originalIban, newName, newEmailAddress);
        String jsonRequest = TestUtils.asJsonString(accountUpdateRequest);
        log.info("Serialized JSON Request: {}", jsonRequest);

        when(accountService.updateAccount(any(AccountUpdateRequest.class)))
                .thenReturn(new Account(TestConstants.TEST_BIC, existingSortCode, newName,
                        existingAcNumber, expectedNewEmailAddress));

        final MvcResult accountUpdateResult = TestUtils.sendPutRequest(accountMockMvc, ACCOUNT_PATH + UPDATE_ACCOUNT_PATH,
                jsonRequest, status().isOk());
        assertNotNull(accountUpdateResult);
        assertNotNull(accountUpdateResult.getResponse());
        final String responseContent = accountUpdateResult.getResponse().getContentAsString();
        validateAccount(responseContent, originalIban, existingAcNumber, existingSortCode, expectedNewName, expectedNewEmailAddress);
        // Verify the amount of times updateAccount() was called
        verify(accountService, times(1)).updateAccount(any(AccountUpdateRequest.class));
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

        final MvcResult mvcResult = TestUtils.sendPutRequest(accountMockMvc, ACCOUNT_PATH + UPDATE_ACCOUNT_PATH,
                jsonRequest, status().isBadRequest());
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(ERROR_MSG_INVALID_IBAN));
        // Verify the amount of times updateAccount() was called
        verify(accountService, times(1)).updateAccount(any(AccountUpdateRequest.class));
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

        final MvcResult mvcResult = TestUtils.sendPutRequest(accountMockMvc, ACCOUNT_PATH + UPDATE_ACCOUNT_PATH,
                jsonRequest, status().isNotFound());
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage), "Error message was actually " + errorResponseString);
        // Verify the amount of times updateAccount() was called
        verify(accountService, times(1)).updateAccount(any(AccountUpdateRequest.class));
    }

    @ParameterizedTest
    @MethodSource("deleteAccountsParameters")
    void deleteAccountsTest(final String originalIban, final String existingSortCode, final String existingAcNumber,
                            final String existingName, final String existingEmailAddress) throws Exception {
        final Account expectedOriginalAccount = new Account(accountServiceProperties.getBankIdentifierCode(),
                existingSortCode, existingName, existingAcNumber, existingEmailAddress);
        expectedOriginalAccount.setIbanCode(originalIban);
        when(accountService.findAccountByIban(originalIban)).thenReturn(expectedOriginalAccount);

        final String expectedDeletionMessage = MSG_DELETION_SUCCESSFUL.formatted(originalIban);
        when(accountService.deleteAccount(originalIban)).thenReturn(expectedDeletionMessage);

        final String deleteEndpoint = ACCOUNT_PATH + DELETE_PATH.replace("{accountIban}", originalIban);
        final MvcResult accountUpdateResult = TestUtils.sendDeleteRequest(accountMockMvc, deleteEndpoint,
                status().isOk());
        assertNotNull(accountUpdateResult);
        assertNotNull(accountUpdateResult.getResponse());
        final String responseContent = accountUpdateResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(expectedDeletionMessage), "Response message was actually " + responseContent);
        // Verify the amount of times deleteAccount() was called
        verify(accountService, times(1)).deleteAccount(originalIban);
    }

    @ParameterizedTest
    @ValueSource(strings = {IBAN_1, IBAN_2, IBAN_3, IBAN_4, IBAN_5, IBAN_6, IBAN_WO_EMAIL, IBAN_INVALID_EMAIL,
            IBAN_WHITESPACE_1, IBAN_WHITESPACE_2, IBAN_WHITESPACE_3})
    void deleteNonExistentAccountsTest(final String nonexistentIban) throws Exception {
        final Account expectedOriginalAccount = new Account(accountServiceProperties.getBankIdentifierCode(), "234567", "Just Sumguy",
                "12345678", "just.sumguy@someemail.com");
        expectedOriginalAccount.setIbanCode(UPDATED_IBAN_1);
        when(accountService.findAccountByIban(UPDATED_IBAN_1)).thenReturn(expectedOriginalAccount);

        final String expectedDeletionMessage = ERROR_MSG_IBAN_NOT_FOUND.formatted(nonexistentIban);
        when(accountService.deleteAccount(nonexistentIban)).thenThrow(new NotFoundException(ERROR_MSG_IBAN_NOT_FOUND.formatted(nonexistentIban)));

        final String deleteEndpoint = ACCOUNT_PATH + DELETE_PATH.replace("{accountIban}", nonexistentIban);
        final MvcResult accountUpdateResult = TestUtils.sendDeleteRequest(accountMockMvc, deleteEndpoint,
                status().isNotFound());
        assertNotNull(accountUpdateResult);
        assertNotNull(accountUpdateResult.getResponse());
        final String responseContent = accountUpdateResult.getResponse().getContentAsString();
        assertTrue(responseContent.contains(expectedDeletionMessage), "Response message was actually " + responseContent);
        // Verify the amount of times deleteAccount() was called
        verify(accountService, times(1)).deleteAccount(nonexistentIban);
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "null", "\t\t", "\n", "      ", "123", "123456767890", TestConstants.IBAN_FAIL_1,
            TestConstants.IBAN_FAIL_2, TestConstants.IBAN_FAIL_3, TestConstants.IBAN_FAIL_4, TestConstants.IBAN_FAIL_5,
            TestConstants.IBAN_FAIL_6, TestConstants.IBAN_FAIL_7})
    void deleteAccountsInvalidIbanTest(final String invalidIban)
            throws Exception {
        when(accountService.deleteAccount(invalidIban))
                .thenThrow(new InvalidArgumentsException(ERROR_MSG_INVALID_IBAN));
        final String deleteEndpoint = ACCOUNT_PATH + DELETE_PATH.replace("{accountIban}", invalidIban);
        final MvcResult mvcResult = TestUtils.sendDeleteRequest(accountMockMvc, deleteEndpoint,
                status().isBadRequest());
        final String errorResponseString = mvcResult.getResponse().getContentAsString();
        assertTrue(errorResponseString.contains(ERROR_MSG_INVALID_IBAN));
        // Verify the amount of times deleteAccount() was called
        verify(accountService, times(1)).deleteAccount(invalidIban);
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

    private static Stream<Arguments> deleteAccountsParameters() {
        return Stream.of(
                DELETE_USER_1_ARGS,
                DELETE_USER_2_ARGS,
                DELETE_USER_3_ARGS,
                DELETE_USER_4_ARGS,
                DELETE_USER_5_ARGS
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