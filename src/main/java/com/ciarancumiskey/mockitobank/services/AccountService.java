package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.AccountUpdateRequest;
import com.ciarancumiskey.mockitobank.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ciarancumiskey.mockitobank.utils.Constants.*;

@Service
@Slf4j
public class AccountService {

    @Autowired private AccountDbRepository accountDbRepository;

    public Account createAccount(final String sortCode, final String accountName, final String accountNumber, final String emailAddress){
        log.info("Creating new account for {}", accountName);
        // Validate the account
        if(!validateAccountDetails(sortCode, accountNumber, accountName)){
            return null;
        }
        final Account newAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        // Verify that the new account doesn't clash with an existing one
        final String newAccountIban = newAccount.getIbanCode();
        if(findAccountByIban(newAccountIban) == null){
            accountDbRepository.save(newAccount);
            return newAccount;
        } else {
            log.error("Account with IBAN {} already exists", newAccountIban);
            return null;
        }
    }

    public Account updateAccount(final AccountUpdateRequest accountUpdateRequest){
        final String existingIbanCode = accountUpdateRequest.getAccountIban();
        final Optional<Account> accountToUpdate = accountDbRepository.findById(existingIbanCode);
        if(accountToUpdate.isEmpty()){
            log.error("No account found with IBAN {}", existingIbanCode);
            return null;
        } else {
            log.info("Updating account for {}", existingIbanCode);
            final Account account = accountToUpdate.get();
            final String accountName = accountUpdateRequest.getAccountName();
            // The update might have missing details, so just skip those missing attributes
            if(accountName.isBlank()) {
               log.error("Updated customer name can't be blank.");
            } else {
                account.setAccountName(accountName);
            }
            final String emailAddress = accountUpdateRequest.getEmailAddress();
            if(EMAIL_REGEX.matcher(emailAddress).find()){
                account.setEmailAddress(emailAddress);
            } else if (EMAIL_REGEX.matcher(emailAddress.trim()).find()) {
                account.setEmailAddress(emailAddress.trim());
            } else {
                log.error("Entered email address {} isn't in the requested format \"username@emailprovider.tld\".", emailAddress);
            }
            account.setIbanCode(MOCKITO_BANK_IBAN_PREFIX + account.getSortCode() + account.getAccountNumber());
            // Overwrite the account
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

    private boolean validateAccountDetails(final String sortCode, final String accountNumber, final String accountName){
        // Validate the inputs
        if(!Constants.SORT_CODE_REGEX.matcher(sortCode).find()){
            log.error("Sort code {} is invalid, it must be 6 numbers and nothing else", sortCode);
            return false;
        }
        if(!Constants.ACCOUNT_NUMBER_REGEX.matcher(accountNumber).find()){
            log.error("Account number {} is invalid, it must be 8 numbers and nothing else", accountNumber);
            return false;
        }
        if(accountName.isBlank()){
            log.error("Please enter a name.");
            return false;
        }
        return true;
    }

    private void validateAccountNewEmail(final Account newAccount, final String emailAddress){
        if(!Constants.EMAIL_REGEX.matcher(emailAddress).find()){
            log.error("Email address \"{}\" is invalid. Saving new account with empty email address instead", emailAddress);
            newAccount.setEmailAddress("");
        } else if (Constants.EMAIL_REGEX.matcher(emailAddress.strip()).find()){
            log.info("Removing trailing whitespace from new customer's email address");
            newAccount.setEmailAddress(emailAddress.strip());
        }
    }
}
