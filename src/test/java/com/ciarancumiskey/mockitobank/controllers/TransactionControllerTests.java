package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.configuration.AccountServiceProperties;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.Transaction;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.models.TransactionResponse;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.services.TransactionService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.models.TransactionType.*;
import static com.ciarancumiskey.mockitobank.utils.Constants.*;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
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

        final TransactionRequest depositRequest = new TransactionRequest(DEPOSIT, recipientIban, "DEPOSIT", amount, "Test");
        final TransactionResponse expectedResponse = new TransactionResponse();
        expectedResponse.updatePayeeBalance(expectedBalance);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = sendTransactionRequest(depositRequest, status().isOk());
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
    void withdrawMoneyTest(final String payerIban, final BigDecimal amount, final BigDecimal expectedBalance) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest withdrawalRequest = new TransactionRequest(WITHDRAWAL, payerIban, "DEPOSIT", amount, "Test");
        final TransactionResponse expectedResponse = new TransactionResponse();
        expectedResponse.updatePayerBalance(expectedBalance);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = sendTransactionRequest(withdrawalRequest, status().isOk());
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
    @MethodSource("transferParameters")
    void transferMoneyTest(final String payerIban, final String recipientIban, final BigDecimal amount,
                           final BigDecimal expectedPayerBalance, final BigDecimal expectedPayeeBalance) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest withdrawalRequest = new TransactionRequest(TRANSFER, recipientIban, payerIban,
                amount, "Test");
        final TransactionResponse expectedResponse = new TransactionResponse();
        expectedResponse.updatePayeeBalance(expectedPayeeBalance);
        expectedResponse.updatePayerBalance(expectedPayerBalance);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenReturn(expectedResponse);

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = sendTransactionRequest(withdrawalRequest, status().isOk());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        final TransactionResponse transactionResponse = (TransactionResponse)
                TestUtils.fromJsonString(depositResultContent, TransactionResponse.class);
        assertNotNull(transactionResponse);
        assertNotNull(transactionResponse.getUpdatedAccountBalances());
        assertEquals(expectedPayeeBalance, transactionResponse.getUpdatedAccountBalances().get("payee"));
        assertEquals(expectedPayerBalance, transactionResponse.getUpdatedAccountBalances().get("payer"));
    }

    @ParameterizedTest
    @MethodSource("withdrawTooMuchParameters")
    void withdrawTooMuchMoneyTest(final String recipientIban, final BigDecimal amount) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest depositRequest = new TransactionRequest(WITHDRAWAL, "WITHDRAWAL", recipientIban, amount, "me money needing a lot now");
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new InvalidArgumentsException(ERROR_MSG_NOT_ENOUGH_MONEY));

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = sendTransactionRequest(depositRequest, status().isBadRequest());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        assertTrue(depositResultContent.contains(ERROR_MSG_NOT_ENOUGH_MONEY));
    }

    @ParameterizedTest
    @MethodSource("transferTooMuchParameters")
    void transferTooMuchMoneyTest(final String payerIban, final String recipientIban, final BigDecimal amount) throws Exception {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        final TransactionRequest depositRequest = new TransactionRequest(TRANSFER, recipientIban, payerIban, amount, "me money needing a lot now");
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new InvalidArgumentsException(ERROR_MSG_NOT_ENOUGH_MONEY));

        // Verify that the account's balance has increased by the deposit amount
        final MvcResult depositMvcResult = sendTransactionRequest(depositRequest, status().isBadRequest());
        assertNotNull(depositMvcResult);
        assertNotNull(depositMvcResult.getResponse());
        final String depositResultContent = depositMvcResult.getResponse().getContentAsString();
        assertFalse(depositResultContent.isBlank());
        assertTrue(depositResultContent.contains(ERROR_MSG_NOT_ENOUGH_MONEY));
    }

    @ParameterizedTest
    @MethodSource("missingAccountParameters")
    void depositWithMissingAccountTests(final String payeeIban, final BigDecimal transactionAmount) throws Exception {
        final TransactionRequest transactionRequest = new TransactionRequest(DEPOSIT, payeeIban, null, transactionAmount, "404 time");
        final String expectedErrorMessage = Constants.ERROR_MSG_PAYEE_NOT_FOUND.formatted(payeeIban);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new NotFoundException(expectedErrorMessage));

        final MvcResult accountGetMvcResult = sendTransactionRequest(transactionRequest, status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage),
                "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(transactionService, times(1)).transferMoney(any(TransactionRequest.class));
    }

    @ParameterizedTest
    @MethodSource("missingAccountParameters")
    void withdrawWithMissingAccountTests(final String nonexistentIban, final BigDecimal transactionAmount) throws Exception {
        final TransactionRequest transactionRequest = new TransactionRequest(WITHDRAWAL, null, nonexistentIban, transactionAmount, "This account shouldn't exist");
        final String expectedErrorMessage = Constants.ERROR_MSG_PAYER_NOT_FOUND.formatted(nonexistentIban);
        when(transactionService.transferMoney(any(TransactionRequest.class))).thenThrow(new NotFoundException(expectedErrorMessage));

        final MvcResult accountGetMvcResult = sendTransactionRequest(transactionRequest, status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage),
                "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(transactionService, times(1)).transferMoney(any(TransactionRequest.class));
    }

    @ParameterizedTest
    @MethodSource("transferToMissingAccountParameters")
    void withdrawWithMissingAccountTests(final String payerIban, final String payeeIban,
                                         final BigDecimal transactionAmount, final String expectedErrorMessage) throws Exception {
        final TransactionRequest transactionRequest = new TransactionRequest(TRANSFER, payeeIban, payerIban,
                transactionAmount, "This account shouldn't exist");
        when(transactionService.transferMoney(any(TransactionRequest.class)))
                .thenThrow(new NotFoundException(expectedErrorMessage));

        final MvcResult accountGetMvcResult = sendTransactionRequest(transactionRequest, status().isNotFound());
        final String errorResponseString = accountGetMvcResult.getResponse().getContentAsString();
        log.info("Error content: {}", errorResponseString);
        assertTrue(errorResponseString.contains(expectedErrorMessage),
                "Error message was actually " + errorResponseString);
        // Verify the amount of times findAccountByIban() was called
        verify(transactionService, times(1)).transferMoney(any(TransactionRequest.class));
    }

    @Test
    void testGettingTransactionHistory(){
        final TransactionRequest transactionReq1 = new TransactionRequest(DEPOSIT, IBAN_1, "", BigDecimal.valueOf(3000), "Salary");
        final TransactionResponse transactionResp1 = new TransactionResponse();
        transactionResp1.updatePayeeBalance(BigDecimal.valueOf(13500));
        final TransactionRequest transactionReq2 = new TransactionRequest(TRANSFER, IBAN_2, IBAN_1, BigDecimal.valueOf(400), "Venmo");
        final TransactionResponse transactionResp2 = new TransactionResponse();
        transactionResp2.updatePayeeBalance(BigDecimal.valueOf(9150));
        transactionResp2.updatePayerBalance(BigDecimal.valueOf(13100));
        final TransactionRequest transactionReq3 = new TransactionRequest(TRANSFER, IBAN_1, IBAN_3, BigDecimal.valueOf(300), "Refund");
        final TransactionResponse transactionResp3 = new TransactionResponse();
        transactionResp3.updatePayeeBalance(BigDecimal.valueOf(13400));
        transactionResp3.updatePayerBalance(BigDecimal.valueOf(8834.63));
        final TransactionRequest transactionReq4 = new TransactionRequest(WITHDRAWAL, "", IBAN_1, BigDecimal.valueOf(3000), "ATM");
        final TransactionResponse transactionResp4 = new TransactionResponse();
        transactionResp4.updatePayerBalance(BigDecimal.valueOf(10400));
        try {
            when(transactionService.transferMoney(transactionReq1)).thenReturn(transactionResp1);
            when(transactionService.transferMoney(transactionReq2)).thenReturn(transactionResp2);
            when(transactionService.transferMoney(transactionReq3)).thenReturn(transactionResp3);
            when(transactionService.transferMoney(transactionReq4)).thenReturn(transactionResp4);

            final Transaction expectedTransaction1 = new Transaction(IBAN_1, "", BigDecimal.valueOf(3000), "Salary");
            final Transaction expectedTransaction2 = new Transaction(IBAN_2, IBAN_1, BigDecimal.valueOf(400), "Venmo");
            final Transaction expectedTransaction3 = new Transaction(IBAN_1, IBAN_3, BigDecimal.valueOf(300), "Refund");
            final Transaction expectedTransaction4 = new Transaction("", IBAN_1, BigDecimal.valueOf(3000), "ATM");

            final List<Transaction> iban1Transactions = List.of(expectedTransaction1,
                    expectedTransaction2, expectedTransaction3, expectedTransaction4);
            when(transactionService.getTransactionHistory(IBAN_1)).thenReturn(iban1Transactions);
            final MvcResult iban1TransactionHistory = TestUtils.sendGetRequest(transactionsMockMvc,
                    TRANSACTIONS_PATH + HISTORY_PATH.replace("{accountIban}", IBAN_1), status().isOk());
            validateTransactionHistoryResponse(iban1TransactionHistory, iban1Transactions);

            final List<Transaction> iban2Transactions = List.of(expectedTransaction2);
            when(transactionService.getTransactionHistory(IBAN_2)).thenReturn(iban2Transactions);
            final MvcResult iban2TransactionHistory = TestUtils.sendGetRequest(transactionsMockMvc,
                    TRANSACTIONS_PATH + HISTORY_PATH.replace("{accountIban}", IBAN_2), status().isOk());
            validateTransactionHistoryResponse(iban2TransactionHistory, iban2Transactions);

            final List<Transaction> iban3Transactions = List.of(expectedTransaction3);
            when(transactionService.getTransactionHistory(IBAN_3)).thenReturn(iban3Transactions);
            final MvcResult iban3TransactionHistory = TestUtils.sendGetRequest(transactionsMockMvc,
                    TRANSACTIONS_PATH + HISTORY_PATH.replace("{accountIban}", IBAN_3), status().isOk());
            validateTransactionHistoryResponse(iban3TransactionHistory, iban3Transactions);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {IBAN_4, IBAN_5, IBAN_6, UPDATED_IBAN_1, IBAN_WHITESPACE_1, IBAN_WHITESPACE_2,
            IBAN_WHITESPACE_3, IBAN_WO_EMAIL, IBAN_INVALID_EMAIL})
    void testGettingTransactionHistoryForNonexistentAccount(final String iban) throws Exception {
        final String expectedErrorMsg = ERROR_MSG_IBAN_NOT_FOUND.formatted(iban);
        when(transactionService.getTransactionHistory(iban)).thenThrow(new NotFoundException(expectedErrorMsg));

        final MvcResult transactionHistoryResult = TestUtils.sendGetRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + HISTORY_PATH.replace("{accountIban}", iban), status().isNotFound());
        MockHttpServletResponse accountNotFoundResponse = transactionHistoryResult.getResponse();
        assertTrue(accountNotFoundResponse.getContentAsString().contains(expectedErrorMsg));
    }

    @ParameterizedTest
    @ValueSource(strings = {IBAN_FAIL_1, IBAN_FAIL_2, IBAN_FAIL_3, IBAN_FAIL_4, IBAN_FAIL_5, IBAN_FAIL_6, " ", "\n", "\t\t"})
    void testGettingTransactionHistoryForInvalidAccount(final String iban) throws Exception {
        when(transactionService.getTransactionHistory(iban)).thenThrow(new InvalidArgumentsException(ERROR_MSG_INVALID_IBAN));

        final MvcResult transactionHistoryResult = TestUtils.sendGetRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + HISTORY_PATH.replace("{accountIban}", iban), status().isBadRequest());
        MockHttpServletResponse accountNotFoundResponse = transactionHistoryResult.getResponse();
        assertTrue(accountNotFoundResponse.getContentAsString().contains(ERROR_MSG_INVALID_IBAN));
    }

    private MvcResult sendTransactionRequest(final TransactionRequest transactionRequest,
                                             final ResultMatcher expectedStatus){
        try {
            return TestUtils.sendPostRequest(transactionsMockMvc,
                    TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(transactionRequest), expectedStatus);
        } catch (final Exception e) {
            log.error("Failed to send transaction request.", e);
            fail();
            throw new RuntimeException(e);
        }
    }

    private void validateTransactionHistoryResponse(final MvcResult transactionHistory, final List<Transaction> expectedTransactions){
        MockHttpServletResponse response = transactionHistory.getResponse();
        try {
            String responseContent = response.getContentAsString();
            assertFalse(responseContent.isBlank());
            final List<Transaction> parsedTransactions = TestUtils.fromJsonStringList(responseContent, Transaction.class);
            assertNotNull(parsedTransactions);
            assertEquals(expectedTransactions.size(), parsedTransactions.size());
            for(final Transaction expectedTx : expectedTransactions) {
                // Verify that the expected transaction is among the parsed transactions from the response
                assertTrue(parsedTransactions.stream().anyMatch(parsedTx ->
                        parsedTx.getAmount().equals(expectedTx.getAmount()) &&
                        parsedTx.getDescription().equals(expectedTx.getDescription()) &&
                        parsedTx.getPayeeAccount().equals(expectedTx.getPayeeAccount()) &&
                        parsedTx.getPayerAccount().equals(expectedTx.getPayerAccount()) &&
                        parsedTx.getTransactionTime().equals(expectedTx.getTransactionTime())));
            }
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
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
    // 10500, 8750, 9134.63, 1814.60
    private static Stream<Arguments> transferParameters() {
        return Stream.of(
                Arguments.of(IBAN_1, IBAN_2, BigDecimal.valueOf(1000), BigDecimal.valueOf(9500),
                        BigDecimal.valueOf(9750)),
                Arguments.of(IBAN_2, IBAN_1, BigDecimal.valueOf(8750), BigDecimal.ZERO, BigDecimal.valueOf(19250)),
                Arguments.of(IBAN_3, IBAN_4, BigDecimal.valueOf(550.50), BigDecimal.valueOf(8584.13),
                        BigDecimal.valueOf(2365.1)),
                Arguments.of(IBAN_4, IBAN_3, BigDecimal.valueOf(897.57), BigDecimal.valueOf(917.03),
                        BigDecimal.valueOf(10032.2)),
                // Test the accounts' overdrafts
                Arguments.of(IBAN_1, IBAN_2, BigDecimal.valueOf(11300), BigDecimal.valueOf(-800),
                        BigDecimal.valueOf(20050)),
                Arguments.of(IBAN_1, IBAN_3, BigDecimal.valueOf(12000), BigDecimal.valueOf(-1500),
                        BigDecimal.valueOf(21134.63)),
                Arguments.of(IBAN_1, IBAN_4, BigDecimal.valueOf(18600.99), BigDecimal.valueOf(-8099.01),
                        BigDecimal.valueOf(20415.59)),
                Arguments.of(IBAN_3, IBAN_1, BigDecimal.valueOf(12300.37), BigDecimal.valueOf(-3165),
                        BigDecimal.valueOf(22800.37)),
                Arguments.of(IBAN_3, IBAN_2, BigDecimal.valueOf(10100.45), BigDecimal.valueOf(-964.92),
                        BigDecimal.valueOf(18850.45)),
                Arguments.of(IBAN_3, IBAN_4, BigDecimal.valueOf(10000), BigDecimal.valueOf(-865.37),
                        BigDecimal.valueOf(11814.6)));
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

    private static Stream<Arguments> transferTooMuchParameters(){
        return Stream.of(
                Arguments.of(IBAN_1, IBAN_2, BigDecimal.valueOf(20000)),
                Arguments.of(IBAN_1, IBAN_3, BigDecimal.valueOf(Long.MAX_VALUE)),
                Arguments.of(IBAN_1, IBAN_4, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_2, IBAN_1, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_2, IBAN_3, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_2, IBAN_4, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_3, IBAN_1, BigDecimal.valueOf(123456)),
                Arguments.of(IBAN_3, IBAN_2, BigDecimal.valueOf(1234567)),
                Arguments.of(IBAN_3, IBAN_4, BigDecimal.valueOf(12345678)),
                Arguments.of(IBAN_4, IBAN_1, BigDecimal.valueOf(54321)),
                Arguments.of(IBAN_4, IBAN_2, BigDecimal.valueOf(123456789)),
                Arguments.of(IBAN_4, IBAN_3, BigDecimal.valueOf(200000))
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

    private static Stream<Arguments> transferToMissingAccountParameters() {
        return Stream.of(
                Arguments.of(IBAN_1, IBAN_5, BigDecimal.valueOf(1000), ERROR_MSG_PAYEE_NOT_FOUND.formatted(IBAN_5)),
                Arguments.of(IBAN_2, IBAN_6, BigDecimal.valueOf(8750), ERROR_MSG_PAYEE_NOT_FOUND.formatted(IBAN_6)),
                Arguments.of(IBAN_3, IBAN_WO_EMAIL, BigDecimal.valueOf(550.50),
                        ERROR_MSG_PAYEE_NOT_FOUND.formatted(IBAN_WO_EMAIL)),
                Arguments.of(IBAN_WHITESPACE_1, IBAN_1, BigDecimal.valueOf(1130),
                        ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_WHITESPACE_1)),
                Arguments.of(IBAN_WHITESPACE_2, IBAN_2, BigDecimal.valueOf(1130),
                        ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_WHITESPACE_2)),
                Arguments.of(IBAN_WHITESPACE_3, IBAN_3, BigDecimal.valueOf(1130),
                        ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_WHITESPACE_3)),
                Arguments.of(IBAN_5, IBAN_6, BigDecimal.valueOf(897.57), ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_5)),
                Arguments.of(IBAN_6, IBAN_5, BigDecimal.valueOf(897.57), ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_6)),
                Arguments.of(IBAN_WHITESPACE_1, IBAN_INVALID_EMAIL, BigDecimal.valueOf(897.57),
                        ERROR_MSG_PAYER_NOT_FOUND.formatted(IBAN_WHITESPACE_1))
                );
    }
}
