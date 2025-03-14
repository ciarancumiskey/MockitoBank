package com.ciarancumiskey.mockitobank.database;

import com.ciarancumiskey.mockitobank.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionDbRepository extends CrudRepository<Transaction, Long> {
    // Empty class so Spring can create a bean for it
}
