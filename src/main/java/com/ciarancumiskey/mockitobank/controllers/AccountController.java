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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@Valid
@RestController
@Slf4j
public class AccountController implements IAccountController {
    @Autowired private AccountService accountService;

    @Override
    public ResponseEntity<Account> createAccount(final AccountCreationRequest accountCreationRequest)
            throws AlreadyExistsException, InvalidArgumentsException {
        final Account newAccount;
        try {
            newAccount = accountService.createAccount(accountCreationRequest.getSortCode(), accountCreationRequest.getAccountName(), accountCreationRequest.getAccountNumber(), accountCreationRequest.getEmailAddress());
            if(newAccount == null) {
                return ResponseEntity.badRequest().build();
            }
            final URI newAccountLocation = URI.create(Constants.ACCOUNT_PATH + "/" + newAccount.getIbanCode());
            return ResponseEntity.created(newAccountLocation).body(newAccount);
        } catch (AlreadyExistsException | InvalidArgumentsException e) {
            log.error("Error when trying to create account: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public ResponseEntity<Account> getAccount(final String accountIban)
            throws InvalidArgumentsException, NotFoundException {
        final Account retrievedAccount;
        try {
            retrievedAccount = accountService.findAccountByIban(accountIban);
        } catch (final InvalidArgumentsException | NotFoundException e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return ResponseEntity.ok(retrievedAccount);
    }

    @Override
    public ResponseEntity<Account> updateAccount(final AccountUpdateRequest accountUpdateRequest) throws InvalidArgumentsException, NotFoundException {
        try {
            final Account updatedAccount = accountService.updateAccount(accountUpdateRequest);
            return ResponseEntity.ok(updatedAccount);
        } catch (final InvalidArgumentsException | NotFoundException e) {
            log.error("Error when trying to update account - {}", e.getMessage(), e);
            throw e;
        }
    }
    // TODO: Delete
}
