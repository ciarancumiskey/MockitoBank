package com.ciarancumiskey.mockitobank.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

import static com.ciarancumiskey.mockitobank.utils.Constants.MOCKITO_BANK_IBAN_PREFIX;

@Getter
@Setter
public class Account {
    @NonNull long accountNumber;
    @NonNull String sortCode;
    @NonNull String ibanCode;
    @NonNull String accountName;
    // Use BigDecimal because double isn't precise enough
    @NonNull BigDecimal balance;

    public Account(final String sortCode, final String accountName, final long accountNumber) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.ibanCode = "%s%s%s".formatted(MOCKITO_BANK_IBAN_PREFIX, sortCode, accountNumber);
        this.balance = BigDecimal.ZERO;
    }
}
