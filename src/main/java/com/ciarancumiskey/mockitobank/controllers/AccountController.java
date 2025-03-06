package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.ciarancumiskey.mockitobank.utils.Constants.ACCOUNT_PATH;
import static com.ciarancumiskey.mockitobank.utils.Constants.REGISTRATION_PATH;

@Controller(ACCOUNT_PATH)
public class AccountController {
    @Autowired private AccountService accountService;

    // TODO: Create
    @PostMapping(value = REGISTRATION_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> createAccount(@NonNull @RequestBody final AccountCreationRequest accountCreationRequest){
        final Account newAccount = accountService.createAccount(accountCreationRequest.getSortCode(), accountCreationRequest.getAccountName(), accountCreationRequest.getAccountNumber(), accountCreationRequest.getEmailAddress());
        return ResponseEntity.ok(newAccount);
    }
    // TODO: Read
    // TODO: Update
    // TODO: Delete
}
