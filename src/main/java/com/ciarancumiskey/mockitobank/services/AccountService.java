package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.exceptions.AlreadyExistsException;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
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

    private final AccountDbRepository accountDbRepository;
    //private final String ibanPrefix;

    @Autowired
    public AccountService(final AccountDbRepository accountDbRepository) {
        this.accountDbRepository = accountDbRepository;
    }

    public Account createAccount(final String sortCode, final String accountName, final String accountNumber, final String emailAddress) throws AlreadyExistsException, InvalidArgumentsException {
        log.info("Creating new account for {}", accountName);
        // Validate the account
        validateAccountDetails(sortCode, accountNumber, accountName);
        final Account newAccount = new Account(sortCode, accountName, accountNumber, emailAddress);
        // Verify that the new account doesn't clash with an existing one
        final String newAccountIban = newAccount.getIbanCode();
        final Optional<Account> existingAccountOpt = accountDbRepository.findById(newAccountIban);
        if(existingAccountOpt.isEmpty()){
            accountDbRepository.save(newAccount);
            return newAccount;
        } else {
            final String accountAlreadyExistsErrorMsg = "Account with IBAN %s already exists".formatted(newAccountIban);
            throw new AlreadyExistsException(accountAlreadyExistsErrorMsg);
        }
    }

    public Account updateAccount(final AccountUpdateRequest accountUpdateRequest)
            throws InvalidArgumentsException, NotFoundException {
        final String existingIbanCode = accountUpdateRequest.getAccountIban();
        if(existingIbanCode.length() != 22){
            throw new InvalidArgumentsException(Constants.ERROR_MSG_INVALID_IBAN);
        }
        final Optional<Account> accountToUpdate = accountDbRepository.findById(existingIbanCode);
        if(accountToUpdate.isEmpty()){
            final String notFoundError = Constants.ERROR_MSG_IBAN_NOT_FOUND.formatted(existingIbanCode);
            throw new NotFoundException(notFoundError);
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

    public Account findAccountByIban(final String iban) throws InvalidArgumentsException, NotFoundException  {
        if(iban == null || iban.length() != 22){
            throw new InvalidArgumentsException(ERROR_MSG_INVALID_IBAN);
        }
        final Optional<Account> accountOpt = accountDbRepository.findById(iban);
        if(accountOpt.isEmpty()){
            throw new NotFoundException(ERROR_MSG_IBAN_NOT_FOUND.formatted(iban));
        }
        return accountOpt.get();
    }

    public String deleteAccount(final String ibanToDelete) throws InvalidArgumentsException, NotFoundException {
        if (ibanToDelete == null || ibanToDelete.length() != 22) {
            throw new InvalidArgumentsException(ERROR_MSG_INVALID_IBAN);
        } else if (accountDbRepository.findById(ibanToDelete).isEmpty()){
            throw new NotFoundException(ERROR_MSG_IBAN_NOT_FOUND.formatted(ibanToDelete));
        }
        log.warn("Deleting account of IBAN {}", ibanToDelete);
        accountDbRepository.deleteById(ibanToDelete);
        return MSG_DELETION_SUCCESSFUL.formatted(ibanToDelete);
    }

    private void validateAccountDetails(final String sortCode, final String accountNumber, final String accountName)
        throws InvalidArgumentsException {
        // Validate the inputs
        final String errorMessage;
        if(!Constants.SORT_CODE_REGEX.matcher(sortCode).find()){
            errorMessage = Constants.ERROR_MSG_INVALID_SORT_CODE.formatted(sortCode);
            throw new InvalidArgumentsException(errorMessage);
        }
        if(!Constants.ACCOUNT_NUMBER_REGEX.matcher(accountNumber).find()){
            errorMessage = Constants.ERROR_MSG_INVALID_AC_NUMBER.formatted(accountNumber);
            throw new InvalidArgumentsException(errorMessage);
        }
        if(accountName.isBlank()){
            errorMessage = Constants.ERROR_MSG_BLANK_AC_NAME;
            throw new InvalidArgumentsException(errorMessage);
        }
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
