package com.ciarancumiskey.mockitobank.controllers;

import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountCreationRequest;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.services.AccountService;
import com.ciarancumiskey.mockitobank.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(path = Constants.ACCOUNT_PATH)
public class AccountController {
    @Autowired private AccountService accountService;

    @PostMapping(value = Constants.REGISTRATION_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> createAccount(@NonNull @RequestBody @Valid final AccountCreationRequest accountCreationRequest){
        final Account newAccount = accountService.createAccount(accountCreationRequest.getSortCode(), accountCreationRequest.getAccountName(), accountCreationRequest.getAccountNumber(), accountCreationRequest.getEmailAddress());
        if(newAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        final URI newAccountLocation = URI.create(Constants.ACCOUNT_PATH + "/" + newAccount.getIbanCode());
        return ResponseEntity.created(newAccountLocation).body(newAccount);
    }

    @GetMapping(value = Constants.LOAD_ACCOUNT_PATH, consumes = "application/json", produces = "application/json")
    public ResponseEntity<Account> getAccount(@NonNull @PathVariable final String accountIban){
        final Account retrievedAccount = accountService.findAccountByIban(accountIban);
        if(retrievedAccount == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(retrievedAccount);
    }

    @PutMapping(value = Constants.UPDATE_ACCOUNT_PATH, consumes = "application/json")
    public ResponseEntity<String> updateAccount(@RequestBody @Valid final AccountUpdateRequest accountUpdateRequest){
        final Account updatedAccount = accountService.updateAccount(accountUpdateRequest);
        if(updatedAccount == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.noContent().build();
    }
    // TODO: Delete
}
