package com.ciarancumiskey.mockitobank.models;

import com.ciarancumiskey.mockitobank.utils.Constants;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;

import static com.ciarancumiskey.mockitobank.utils.Constants.MOCKITO_BANK_IBAN_PREFIX;

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
    //todo: transaction history
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
    }

    public boolean deposit(final BigDecimal depositAmount){
        if(depositAmount.doubleValue() < 0) {
            log.error("You can't deposit a negative amount.");
            return false;
        }
        boolean depositSuccessful = adjustBalance(depositAmount);
        return depositSuccessful;
    }

    public boolean withdraw(final BigDecimal withdrawalAmount){
        if(withdrawalAmount.doubleValue() < 0){
            log.error("You can't withdraw a negative amount.");
            return false;
        }
        final BigDecimal adjustmentAmount = BigDecimal.ZERO.subtract(withdrawalAmount);
        boolean withdrawalSuccessful = adjustBalance(adjustmentAmount);
        return withdrawalSuccessful;
    }

    private boolean adjustBalance(final BigDecimal adjustmentAmount) {
        if((this.balance.doubleValue() + adjustmentAmount.doubleValue()) < BigDecimal.ZERO.subtract(this.overdraftLimit).doubleValue()){
            log.error("Withdrawal rejected, your balance would be {} and exceed your overdraft of {}.", (this.balance.doubleValue() + adjustmentAmount.doubleValue()), this.overdraftLimit);
            return false;
        }
        this.balance.add(adjustmentAmount);
        return true;
    }
}
