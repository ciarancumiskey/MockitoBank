package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import static com.ciarancumiskey.mockitobank.utils.Constants.g;

@Valid
@RestController
@Slf4j
public class TransactionController implements ITransactionController {

    @Override
    public String transferMoney(TransactionRequest transactionRequest) {
        // TODO: Create service class
        return g.toJson(transactionRequest);
    }
}
