package com.ciarancumiskey.mockitobank.database;

import com.ciarancumiskey.mockitobank.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface TransactionDbRepository extends CrudRepository<Transaction, Long> {
    // Empty class so Spring can create a bean for it

    default List<Transaction> findAllByPayee(final String payeeIban){
        final List<Transaction> payeeTransactions = new ArrayList<>();
        Iterable<Transaction> all = findAll();
        all.forEach(transaction -> {
            if(payeeIban.equals(transaction.getPayeeAccount())) {
                payeeTransactions.add(transaction);
            }
        });
        return payeeTransactions;
    }

    default List<Transaction> findAllByPayer(final String payerIban){
        final List<Transaction> payerTransactions = new ArrayList<>();
        Iterable<Transaction> all = findAll();
        all.forEach(transaction -> {
            if(payerIban.equals(transaction.getPayerAccount())) {
                payerTransactions.add(transaction);
            }
        });
        return payerTransactions;
    }
}
