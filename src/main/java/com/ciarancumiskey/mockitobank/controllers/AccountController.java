package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.ciarancumiskey.mockitobank.utils.Constants.ACCOUNT_PATH;
import static com.ciarancumiskey.mockitobank.utils.Constants.REGISTRATION_PATH;

@RestController
@RequestMapping(path = ACCOUNT_PATH)
public class AccountController {
    @Autowired private AccountService accountService;

    @PostMapping(value = REGISTRATION_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> createAccount(@NonNull @RequestBody final AccountCreationRequest accountCreationRequest){
        final Account newAccount = accountService.createAccount(accountCreationRequest.getSortCode(), accountCreationRequest.getAccountName(), accountCreationRequest.getAccountNumber(), accountCreationRequest.getEmailAddress());
        if(newAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        final URI newAccountLocation = URI.create(ACCOUNT_PATH + "/" + newAccount.getIbanCode());
        return ResponseEntity.created(newAccountLocation).body(newAccount);
    }

    @GetMapping(value = "/load", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> getAccount(@NonNull @RequestBody final String accountIban){
        final Account retrievedAccount = accountService.findAccountByIban(accountIban);
        if(retrievedAccount == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(retrievedAccount);
    }

    @PutMapping(value = "/update", consumes = "application/json", produces = "application/json")
    public ResponseEntity.HeadersBuilder<?> updateAccount(@RequestBody final AccountUpdateRequest accountCreationRequest){
        final Account updatedAccount = accountService.updateAccount(accountCreationRequest);
        if(updatedAccount == null) {
            return ResponseEntity.badRequest();
        }
        return ResponseEntity.noContent();
    }
    // TODO: Delete
}
