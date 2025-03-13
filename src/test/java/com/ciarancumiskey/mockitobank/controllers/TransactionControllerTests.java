package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.configuration.AccountServiceProperties;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.models.TransactionResponse;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.services.TransactionService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.models.TransactionType.DEPOSIT;
import static com.ciarancumiskey.mockitobank.models.TransactionType.WITHDRAWAL;
import static com.ciarancumiskey.mockitobank.utils.Constants.*;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.AC_NUMBER_1;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.AC_NUMBER_2;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.AC_NUMBER_3;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_1;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_2;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_3;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_4;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_5;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_6;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_INVALID_EMAIL;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_WHITESPACE_1;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_WHITESPACE_2;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_WHITESPACE_3;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.IBAN_WO_EMAIL;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.SORT_CODE_1;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.SORT_CODE_2;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.SORT_CODE_3;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.SORT_CODE_4;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.TEST_BIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(TransactionController.class)
public class TransactionControllerTests {
    @Autowired
    private MockMvc transactionsMockMvc;

    // Needed for account management
    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private TransactionService transactionService;

    @Mock
    private AccountServiceProperties accountServiceProperties;


    @BeforeEach
    void setUp() {
        // Set up the IBAN prefix for the test
        when(accountServiceProperties.getBankIdentifierCode()).thenReturn(TEST_BIC);
        final Account user1 = new Account(TEST_BIC, SORT_CODE_1, "Testina Hendricks", AC_NUMBER_1,
                "testina.h@testmail.com");
        user1.setBalance(BigDecimal.valueOf(10500));
        user1.setOverdraftLimit(BigDecimal.valueOf(10000));
        final Account user2 = new Account(TEST_BIC, SORT_CODE_2, "Testopher Walken", AC_NUMBER_2,
                "testopher.walken@testmail.com");
        user2.setBalance(BigDecimal.valueOf(8750));
        // don't give user2 an overdraft
        final Account user3 = new Account(TEST_BIC, SORT_CODE_3, "Testoph Waltz", AC_NUMBER_3,
                "testophwaltz@testmail.at");
        user3.setBalance(BigDecimal.valueOf(9134.63));
        user3.setOverdraftLimit(BigDecimal.valueOf(5000));
        final Account user4 = new Account(TEST_BIC, SORT_CODE_4, "Luca Badoer", AC_NUMBER_1,
                "lucabadoer@maranello.it");
        user4.setBalance(BigDecimal.valueOf(1814.60));
        // don't give user4 an overdraft
        try {
            when(accountService.findAccountByIban(IBAN_1)).thenReturn(user1);
            when(accountService.findAccountByIban(IBAN_2)).thenReturn(user2);
            when(accountService.findAccountByIban(IBAN_3)).thenReturn(user3);
            when(accountService.findAccountByIban(IBAN_4)).thenReturn(user4);
        } catch (InvalidArgumentsException | NotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @MethodSource("depositParameters")
    void depositMoneyTest(final String recipientIban, final BigDecimal amount, final BigDecimal expectedBalance) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest depositRequest = new TransactionRequest(DEPOSIT, recipientIban, "DEPOSIT", amount);
        final TransactionResponse expectedResponse = new TransactionResponse();
        expectedResponse.updatePayeeBalance(expectedBalance);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(depositRequest), status().isOk());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        final TransactionResponse transactionResponse = (TransactionResponse)
                TestUtils.fromJsonString(depositResultContent, TransactionResponse.class);
        assertNotNull(transactionResponse);
        assertNotNull(transactionResponse.getUpdatedAccountBalances());
        assertEquals(expectedBalance, transactionResponse.getUpdatedAccountBalances().get("payee"));
    }

    @ParameterizedTest
    @MethodSource("withdrawalParameters")
    void withdrawMoneyTest(final String recipientIban, final BigDecimal amount, final BigDecimal expectedBalance) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest depositRequest = new TransactionRequest(WITHDRAWAL, recipientIban, "DEPOSIT", amount);
        final TransactionResponse expectedResponse = new TransactionResponse();
        expectedResponse.updatePayerBalance(expectedBalance);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(depositRequest), status().isOk());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        final TransactionResponse transactionResponse = (TransactionResponse)
                TestUtils.fromJsonString(depositResultContent, TransactionResponse.class);
        assertNotNull(transactionResponse);
        assertNotNull(transactionResponse.getUpdatedAccountBalances());
        assertEquals(expectedBalance, transactionResponse.getUpdatedAccountBalances().get("payer"));
    }

    @ParameterizedTest
    @MethodSource("withdrawTooMuchParameters")
    void withdrawTooMuchMoneyTest(final String recipientIban, final BigDecimal amount) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest depositRequest = new TransactionRequest(WITHDRAWAL, recipientIban, "DEPOSIT", amount);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new InvalidArgumentsException(ERROR_MSG_NOT_ENOUGH_MONEY));

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(depositRequest), status().isBadRequest());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        assertTrue(depositResultContent.contains(ERROR_MSG_NOT_ENOUGH_MONEY));
    }

    @ParameterizedTest
    @MethodSource("missingAccountParameters")
    void depositWithMissingAccountTests(final String payeeIban, final BigDecimal transactionAmount) throws Exception {
        final TransactionRequest transactionRequest = new TransactionRequest(DEPOSIT, payeeIban, null, transactionAmount);
        final String expectedErrorMessage = Constants.ERROR_MSG_PAYEE_NOT_FOUND.formatted(payeeIban);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new NotFoundException(expectedErrorMessage));

        final MvcResult accountGetMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(transactionRequest), status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage),
                "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(transactionService, times(1)).transferMoney(any(TransactionRequest.class));
    }

    @ParameterizedTest
    @MethodSource("missingAccountParameters")
    void withdrawWithMissingAccountTests(final String payeeIban, final BigDecimal transactionAmount) throws Exception {
        final TransactionRequest transactionRequest = new TransactionRequest(WITHDRAWAL, payeeIban, null, transactionAmount);
        final String expectedErrorMessage = Constants.ERROR_MSG_PAYER_NOT_FOUND.formatted(payeeIban);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new NotFoundException(expectedErrorMessage));

        final MvcResult accountGetMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(transactionRequest), status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage),
                "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(transactionService, times(1)).transferMoney(any(TransactionRequest.class));
    }

    private static Stream<Arguments> depositParameters() {
        return Stream.of(
                Arguments.of(IBAN_1, BigDecimal.valueOf(1000), BigDecimal.valueOf(11500)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(8750), BigDecimal.valueOf(17500)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(550.50), BigDecimal.valueOf(9685.13)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(897.57), BigDecimal.valueOf(2712.17)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(1130), BigDecimal.valueOf(11630)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(1303), BigDecimal.valueOf(10353)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(2025.03), BigDecimal.valueOf(1159.66)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(313.32), BigDecimal.valueOf(2127.92))
        );
    }

    private static Stream<Arguments> withdrawalParameters() {
        return Stream.of(
                Arguments.of(IBAN_1, BigDecimal.valueOf(1000), BigDecimal.valueOf(9500)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(8750), BigDecimal.ZERO),
                Arguments.of(IBAN_3, BigDecimal.valueOf(550.50), BigDecimal.valueOf(8584.13)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(897.57), BigDecimal.valueOf(917.03)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(1130), BigDecimal.valueOf(9370)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(1303), BigDecimal.valueOf(7747)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(25.03), BigDecimal.valueOf(1109.6)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(313.32), BigDecimal.valueOf(1501.28)),
                // Test the accounts' overdrafts
                Arguments.of(IBAN_1, BigDecimal.valueOf(11300), BigDecimal.valueOf(-800)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(12000), BigDecimal.valueOf(-1500)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(18600.99), BigDecimal.valueOf(-8099.01)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(12300.37), BigDecimal.valueOf(-3165)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(10100.45), BigDecimal.valueOf(-964.92)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(10000), BigDecimal.valueOf(-865.37)));
    }

    private static Stream<Arguments> withdrawTooMuchParameters(){
        return Stream.of(
                Arguments.of(IBAN_1, BigDecimal.valueOf(20000)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(Long.MAX_VALUE)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_1, BigDecimal.valueOf(123456789)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_2, BigDecimal.valueOf(123456789)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_3, BigDecimal.valueOf(123456789)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(123456789)),
                Arguments.of(IBAN_4, BigDecimal.valueOf(200000))
        );
    }

    private static Stream<Arguments> missingAccountParameters() {
        return Stream.of(
                Arguments.of(IBAN_5, BigDecimal.valueOf(1000)),
                Arguments.of(IBAN_6, BigDecimal.valueOf(8750)),
                Arguments.of(IBAN_WO_EMAIL, BigDecimal.valueOf(550.50)),
                Arguments.of(IBAN_INVALID_EMAIL, BigDecimal.valueOf(897.57)),
                Arguments.of(IBAN_WHITESPACE_1, BigDecimal.valueOf(1130)),
                Arguments.of(IBAN_WHITESPACE_2, BigDecimal.valueOf(1130)),
                Arguments.of(IBAN_WHITESPACE_3, BigDecimal.valueOf(1130))
        );
    }
}
