package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.configuration.AccountServiceProperties;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.Transaction;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.TestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static com.ciarancumiskey.mockitobank.utils.Constants.TRANSACTIONS_PATH;
import static com.ciarancumiskey.mockitobank.utils.Constants.TRANSFER_PATH;
import static com.ciarancumiskey.mockitobank.utils.TestConstants.*;
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

    @Mock
    private AccountServiceProperties accountServiceProperties;


    @BeforeEach
    void setUp() {
        // Set up the IBAN prefix for the test
        when(accountServiceProperties.getBankIdentifierCode()).thenReturn(TEST_BIC);
        final Account user1 = new Account(TEST_BIC, SORT_CODE_1, "Testina Hendricks", AC_NUMBER_1,
                "testina.h@testmail.com");
        user1.setBalance(BigDecimal.valueOf(10500));
        final Account user2 = new Account(TEST_BIC, SORT_CODE_2, "Testopher Walken", AC_NUMBER_2,
                "testopher.walken@testmail.com");
        user2.setBalance(BigDecimal.valueOf(8750));
        final Account user3 = new Account(TEST_BIC, SORT_CODE_3, "Testoph Waltz", AC_NUMBER_3,
                "testophwaltz@testmail.at");
        user3.setBalance(BigDecimal.valueOf(9134.63));
        final Account user4 = new Account(TEST_BIC, SORT_CODE_4, "Luca Badoer", AC_NUMBER_1,
                "lucabadoer@maranello.it");
        user4.setBalance(BigDecimal.valueOf(1814.60));
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
        final TransactionRequest depositRequest = new TransactionRequest(recipientIban, "DEPOSIT", amount);
        final MvcResult depositMvcResult = TestUtils.sendPostRequest(transactionsMockMvc,
                TRANSACTIONS_PATH + TRANSFER_PATH, TestUtils.asJsonString(depositRequest), status().isOk());
        //todo verify that the account's balance has increased by the deposit amount
    }

    private static Stream<Arguments> depositParameters() {
        return Stream.of(
                Arguments.of(IBAN_1, BigDecimal.valueOf(1000), BigDecimal.valueOf(11500))
        );
    }
}
