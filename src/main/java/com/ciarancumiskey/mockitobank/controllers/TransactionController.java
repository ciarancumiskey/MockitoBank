package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Transaction;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.models.TransactionResponse;
import com.ciarancumiskey.mockitobank.services.TransactionService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import static com.ciarancumiskey.mockitobank.utils.Constants.g;

@Valid
@RestController
@Slf4j
public class TransactionController implements ITransactionController {
    @Autowired private TransactionService transactionService;

    @Override
    public String transferMoney(TransactionRequest transactionRequest)
            throws InvalidArgumentsException, NotFoundException {
        final TransactionResponse transactionResponse = transactionService.transferMoney(transactionRequest);
        return g.toJson(transactionResponse);
    }

    @Override
    public String getTransactionHistory(String accountIban) throws InvalidArgumentsException, NotFoundException {
        final List<Transaction> transactionList = transactionService.getTransactionHistory(accountIban);
        return g.toJson(transactionList);
    }
}
