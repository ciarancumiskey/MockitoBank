package com.ciarancumiskey.mockitobank.models;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

import static com.ciarancumiskey.mockitobank.utils.Constants.MOCKITO_BANK_IBAN_PREFIX;

@Getter
@Setter
@Slf4j
public class Account {
    // accountNumber and sortCode could start with a 0
    @NonNull String accountNumber;
    @NonNull String sortCode;
    @NonNull String ibanCode;
    @NonNull String accountName;
    // Use BigDecimal because double isn't precise enough
    @NonNull BigDecimal balance;
    @NonNull BigDecimal overdraftLimit;

    public Account(final String sortCode, final String accountName, final String accountNumber) {
        this.accountName = accountName;
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.ibanCode = "%s%s%s".formatted(MOCKITO_BANK_IBAN_PREFIX, sortCode, accountNumber);
        this.balance = BigDecimal.ZERO;
        this.overdraftLimit = BigDecimal.ZERO;
    }

    public boolean deposit(final BigDecimal depositAmount){
        if(depositAmount.doubleValue() < 0) {
            log.error("You can't deposit a negative amount.");
            return false;
        }
        this.balance.add(depositAmount);
        return true;
    }

    public boolean withdraw(final BigDecimal withdrawalAmount){
        BigDecimal withdrawalLimit = balance.add(overdraftLimit);
        if(withdrawalLimit.doubleValue() < withdrawalAmount.doubleValue()) {
            log.error("Requested withdrawal amount {} exceeds balance and overdraft limit of {}", withdrawalAmount, withdrawalLimit);
            return false;
        } else {
            balance.subtract(withdrawalAmount);
            return true;
        }
    }
}
