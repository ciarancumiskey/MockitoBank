package com.ciarancumiskey.mockitobank.services;

import com.ciarancumiskey.mockitobank.database.AccountDbRepository;
import com.ciarancumiskey.mockitobank.database.TransactionDbRepository;
import com.ciarancumiskey.mockitobank.exceptions.InvalidArgumentsException;
import com.ciarancumiskey.mockitobank.exceptions.NotFoundException;
import com.ciarancumiskey.mockitobank.models.Account;
import com.ciarancumiskey.mockitobank.models.Transaction;
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
    private final TransactionDbRepository transactionDbRepository;

    public TransactionService(final AccountDbRepository accountDbRepository,
                              final TransactionDbRepository transactionDbRepository) {
        this.accountDbRepository = accountDbRepository;
        this.transactionDbRepository = transactionDbRepository;
    }

    public TransactionResponse transferMoney(final TransactionRequest transactionRequest)
            throws InvalidArgumentsException, NotFoundException {
        final TransactionResponse transactionResponse = new TransactionResponse();
        switch(transactionRequest.getTransactionType()){
            case DEPOSIT:
                depositMoney(transactionRequest, transactionResponse);
                break;
            case TRANSFER:
                break;
            case WITHDRAWAL:
                withdrawMoney(transactionRequest, transactionResponse);
                break;
        }
        return transactionResponse;
    }

    private void depositMoney(final TransactionRequest transactionRequest,
                              final TransactionResponse transactionResponse) throws NotFoundException {
        final BigDecimal transactionAmount = transactionRequest.getAmount();
        final String payeeIban = transactionRequest.getPayee();
        final Account payeeAccount = verifyTransactionParty(payeeIban, ERROR_MSG_PAYEE_NOT_FOUND.formatted(payeeIban));
        final BigDecimal existingBalance = payeeAccount.getBalance();
        final BigDecimal updatedBalance = existingBalance.add(transactionAmount);
        payeeAccount.setBalance(updatedBalance);
        transactionDbRepository.save(new Transaction(payeeIban, transactionRequest.getTransactionType().name(),
                transactionAmount, transactionRequest.getDescription()));
        accountDbRepository.save(payeeAccount);
        transactionResponse.updatePayeeBalance(updatedBalance);
    }

    private void withdrawMoney(final TransactionRequest transactionRequest,
                              final TransactionResponse transactionResponse) throws NotFoundException, InvalidArgumentsException {
        final BigDecimal transactionAmount = transactionRequest.getAmount();
        final String payerIban = transactionRequest.getPayer();
        final Account payerAccount = verifyTransactionParty(payerIban, ERROR_MSG_PAYER_NOT_FOUND.formatted(payerIban));
        final BigDecimal existingBalance = payerAccount.getBalance();
        final BigDecimal payerOverdraftLimit = payerAccount.getOverdraftLimit();
        final BigDecimal updatedBalance = existingBalance.subtract(transactionAmount);
        // Check if the payer is able to pay
        if(BigDecimal.ZERO.subtract(payerOverdraftLimit).doubleValue() < updatedBalance.doubleValue()) {
            payerAccount.setBalance(updatedBalance);
            transactionDbRepository.save(new Transaction(transactionRequest.getTransactionType().name(), payerIban,
                    transactionAmount, transactionRequest.getDescription()));
            accountDbRepository.save(payerAccount);
            transactionResponse.updatePayerBalance(updatedBalance);
        } else {
            //todo: create a new exception class
            throw new InvalidArgumentsException(ERROR_MSG_NOT_ENOUGH_MONEY);
        }
    }

    private Account verifyTransactionParty(final String ibanToFind, final String errorMsgIfNotFound) throws NotFoundException {
        final Optional<Account> optTransactionPartyAc = accountDbRepository.findById(ibanToFind);
        if(optTransactionPartyAc.isEmpty()){
            throw new NotFoundException(errorMsgIfNotFound);
        } else {
            return optTransactionPartyAc.get();
        }
    }
}
