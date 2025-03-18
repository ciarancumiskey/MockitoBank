package com.ciarancumiskey.mockitobank.database;

import com.ciarancumiskey.mockitobank.models.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDbRepository extends CrudRepository<Account, String> {
    // Empty class so Spring can create a bean for it
}
