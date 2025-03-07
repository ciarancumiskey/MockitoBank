package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import com.ciarancumiskey.mockitobank.utils.TestConstants;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
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

        accountMockMvc.perform(MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtils.asJsonString(accountCreationReq)))
                .andExpect(status().isCreated());
                //.andExpect(MockMvcResultMatchers.content().json());
//        final ResponseEntity<Account> createdAccountResponse = accountMvc.createAccount(accountCreationReq);
//        assertEquals(HttpStatus.CREATED, createdAccountResponse.getStatusCode());
//        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, expectedAccountName,
//                expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void createDuplicateAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                            final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                            final String expectedIbanCode) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account existingAccount = new Account(sortCode, "Oran Ginal", accountNumber,
                "oran.ginal@duplicates.com");
        existingAccount.setIbanCode(expectedIbanCode);

        // Try sending another request to create an account with the same sort code and account number
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);
        accountMockMvc.perform(
                MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                        .content(TestUtils.asJsonString(accountCreationReq))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("createInvalidAccountParameters")
    void createAccountsTestErrors(final String sortCode, final String accountName, final String accountNumber,
                                  final String emailAddress, final String expectedIban) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final AccountCreationRequest accountCreationReq = new AccountCreationRequest(sortCode, accountName,
                accountNumber, emailAddress);

        accountMockMvc.perform(MockMvcRequestBuilders.post(Constants.ACCOUNT_PATH + Constants.REGISTRATION_PATH)
                        .content(TestUtils.asJsonString(accountCreationReq))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        final Account accountFromDb = accountService.findAccountByIban(expectedIban);
        assertNull(accountFromDb);
    }

    @ParameterizedTest
    @MethodSource("createAccountsParameters")
    void getAccountsTest(final String sortCode, final String accountName, final String expectedAccountName,
                         final String accountNumber, final String emailAddress, final String expectedEmailAddress,
                         final String expectedIbanCode) throws Exception {
        final Account expectedAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        expectedAccount.setIbanCode(expectedIbanCode);
        when(accountService.findAccountByIban(expectedIbanCode)).thenReturn(expectedAccount);

        accountMockMvc.perform(MockMvcRequestBuilders.get(Constants.ACCOUNT_PATH + Constants.LOAD_ACCOUNT_PATH.replace("{accountIban}", expectedIbanCode))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
//        assertThat(createdAccountResponse.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
//        validateAccount(createdAccountResponse, expectedIbanCode, accountNumber, sortCode, expectedAccountName,
                //expectedEmailAddress);
    }

    @ParameterizedTest
    @MethodSource("updateAccountsParameters")
    void updateAccountsTest(final String originalIban, final String existingSortCode, final String existingAcNumber,
                            final String existingName, final String newName, final String expectedNewName,
                            final String existingEmailAddress, final String newEmailAddress,
                            final String expectedNewEmailAddress) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        final Account expectedOriginalAccount = new Account(existingSortCode, existingName, existingAcNumber, existingEmailAddress);
        expectedOriginalAccount.setIbanCode(originalIban);
        when(accountService.findAccountByIban(originalIban)).thenReturn(expectedOriginalAccount);

        final AccountUpdateRequest accountUpdateRequest = new AccountUpdateRequest(originalIban, newName, newEmailAddress);
        accountMockMvc.perform(MockMvcRequestBuilders.put(Constants.ACCOUNT_PATH + Constants.UPDATE_ACCOUNT_PATH)
                        .content(TestUtils.asJsonString(accountUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

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
                Arguments.of(TestConstants.IBAN_1, "123456", "12345678", "Joe Bloggs", "Joseph Bloggs ", "Joseph Bloggs", "jb@blahmail.com",
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