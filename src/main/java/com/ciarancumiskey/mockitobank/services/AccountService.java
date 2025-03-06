package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.models.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ciarancumiskey.mockitobank.utils.Constants.MOCKITO_BANK_IBAN_PREFIX;

@Service
@Slf4j
public class AccountService {

    @Autowired private AccountDbRepository accountDbRepository;

    public Account createAccount(final String sortCode, final String accountName, final String accountNumber, final String emailAddress){
        log.info("Creating new account for {}", accountName);
        //TODO: Validation
        final Account newAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        accountDbRepository.save(newAccount);
        return newAccount;
    }

    public Account updateAccount(final String existingIbanCode, final String sortCode, final String accountName, final String accountNumber){
        log.info("Updating account for {}", existingIbanCode);
        final Optional<Account> accountToUpdate = accountDbRepository.findById(existingIbanCode);
        if(accountToUpdate.isEmpty()){
            return null;
        } else {
            final Account account = accountToUpdate.get();
            account.setAccountName(accountName);
            account.setAccountNumber(accountNumber);
            account.setSortCode(sortCode);
            account.setIbanCode("%s%s%s".formatted(MOCKITO_BANK_IBAN_PREFIX, sortCode, accountNumber));
            accountDbRepository.deleteById(existingIbanCode);
            accountDbRepository.save(account);
            return account;
        }
    }

    public Account findAccountByIban(final String iban){
        final Optional<Account> accountOpt = accountDbRepository.findById(iban);
        return accountOpt.orElse(null);
    }

    public String deleteAccount(final String ibanToDelete){
        log.warn("Deleting account of IBAN {}", ibanToDelete);
        accountDbRepository.deleteById(ibanToDelete);
        return ibanToDelete;
    }
}
