package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.exceptions.AlreadyExistsException;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

import static com.ciarancumiskey.mockitobank.utils.Constants.g;

@Valid
@RestController
@Slf4j
public class AccountController implements IAccountController {
    @Autowired private AccountService accountService;

    @Override
    public String createAccount(final AccountCreationRequest accountCreationRequest)
            throws AlreadyExistsException, InvalidArgumentsException {
        final Account newAccount;
        try {
            newAccount = accountService.createAccount(accountCreationRequest.getSortCode(), accountCreationRequest.getAccountName(), accountCreationRequest.getAccountNumber(), accountCreationRequest.getEmailAddress());
            return g.toJson(newAccount);
        } catch (AlreadyExistsException | InvalidArgumentsException e) {
            log.error("Error when trying to create account: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String getAccount(final String accountIban)
            throws InvalidArgumentsException, NotFoundException {
        final Account retrievedAccount;
        try {
            retrievedAccount = accountService.findAccountByIban(accountIban);
        } catch (final InvalidArgumentsException | NotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return g.toJson(retrievedAccount);
    }

    @Override
    public String updateAccount(final AccountUpdateRequest accountUpdateRequest) throws InvalidArgumentsException, NotFoundException {
        try {
            final Account updatedAccount = accountService.updateAccount(accountUpdateRequest);
            return g.toJson(updatedAccount);
        } catch (final InvalidArgumentsException | NotFoundException e) {
            log.error("Error when trying to update account - {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public String deleteAccount(String accountIban) throws NotFoundException, InvalidArgumentsException {
        return accountService.deleteAccount(accountIban);
    }
    // TODO: Delete
}
