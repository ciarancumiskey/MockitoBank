package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.TransactionRequest;
import com.ciarancumiskey.mockitobank.models.TransactionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

import static com.ciarancumiskey.mockitobank.utils.Constants.*;

@Service
@Slf4j
public class TransactionService {
    // Needed for implementing transactions
    private final AccountDbRepository accountDbRepository;
    // todo TransactionDbRepository

    public TransactionService(final AccountDbRepository accountDbRepository) {
        this.accountDbRepository = accountDbRepository;
    }

    public TransactionResponse transferMoney(final TransactionRequest transactionRequest) throws NotFoundException {
        final BigDecimal transactionAmount = transactionRequest.getAmount();
        final BigDecimal existingBalance;
        final BigDecimal updatedBalance;
        final BigDecimal payerOverdraftLimit;
        final TransactionResponse transactionResponse = new TransactionResponse();
        switch(transactionRequest.getTransactionType()){
            case DEPOSIT:
                final Account payeeAccount = verifyTransactionParty(transactionRequest.getPayee(), ERROR_MSG_PAYEE_NOT_FOUND.formatted(
                        transactionRequest.getPayee()));
                existingBalance = payeeAccount.getBalance();
                updatedBalance = existingBalance.add(transactionAmount);
                payeeAccount.setBalance(updatedBalance);
                transactionResponse.getUpdatedAccountBalances().put("payee", updatedBalance);
                break;
            case TRANSFER:
                break;
            case WITHDRAWAL:
                final Account payerAccount = verifyTransactionParty(transactionRequest.getPayer(), ERROR_MSG_PAYER_NOT_FOUND.formatted(
                        transactionRequest.getPayer()));
                existingBalance = payerAccount.getBalance();
                payerOverdraftLimit = payerAccount.getOverdraftLimit();
                updatedBalance = existingBalance.subtract(transactionAmount);
                // Check if the payer is able to pay
                if(BigDecimal.ZERO.subtract(payerOverdraftLimit).doubleValue() < updatedBalance.doubleValue()) {
                    payerAccount.setBalance(updatedBalance);
                    transactionResponse.getUpdatedAccountBalances().put("payer", updatedBalance);
                } else {
                    //todo: create a new exception class
                    throw new RuntimeException(ERROR_MSG_NOT_ENOUGH_MONEY);
                }
                break;
        }
        return transactionResponse;
    }

    private Account verifyTransactionParty(final String ibanToFind, final String errorMsgIfNotFound) throws NotFoundException {
        final Optional<Account> optTransactionPartyAc = accountDbRepository.findById(ibanToFind);
        final Account transactionPartyAc = optTransactionPartyAc.orElseGet(null);
        if(transactionPartyAc == null){
            throw new NotFoundException(errorMsgIfNotFound);
        }
        return transactionPartyAc;
    }
}
