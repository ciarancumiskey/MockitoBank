package com.ciarancumiskey.mockitobank.models;

import com.ciarancumiskey.mockitobank.utils.Constants;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Slf4j
@Entity
public class Account {
    // accountNumber and sortCode could start with a 0
    @Id @NonNull String ibanCode;
    @NonNull String accountNumber;
    @NonNull String sortCode;
    @NonNull String accountName;
    // Use BigDecimal because double isn't precise enough
    @NonNull BigDecimal balance;
    @NonNull BigDecimal overdraftLimit;
    String emailAddress;
    @NonNull final LocalDateTime timeAccountCreated;
    //todo: password

    public Account(@NonNull final String bankIdentifierCode, @NonNull final String sortCode, @NonNull final String accountName,
                   @NonNull final String accountNumber, final String emailAddress) {
        // Remove trailing whitespaces
        this.accountName = accountName.strip();
        this.accountNumber = accountNumber;
        this.sortCode = sortCode;
        this.ibanCode = bankIdentifierCode + sortCode + accountNumber;
        this.balance = BigDecimal.ZERO;
        this.overdraftLimit = BigDecimal.ZERO;
        this.timeAccountCreated = LocalDateTime.now();
        if(Constants.EMAIL_REGEX.matcher(emailAddress).find()){
            this.emailAddress = emailAddress;
        } else {
            if (Constants.EMAIL_REGEX.matcher(emailAddress.strip()).find()) {
                this.emailAddress = emailAddress.strip();
            } else {
                this.emailAddress = "";
            }
        }
    }

    protected Account(){
        // Required for JPA entity
        this.timeAccountCreated = LocalDateTime.now();
    }
}
