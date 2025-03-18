package com.ciarancumiskey.mockitobank.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private final String payeeAccount; // can be null to denote ATM withdrawal
    private final String payerAccount; // can be null to denote ATM deposit
    private final BigDecimal amount;
    private final String description;
    @NonNull private final LocalDateTime transactionTime;

    public Transaction(final String payee, final String payer, final BigDecimal amount,
                       final String description){
        this.payeeAccount = payee;
        this.payerAccount = payer;
        this.amount = amount;
        this.description = description;
        this.transactionTime = LocalDateTime.now();
    }

    protected Transaction() {
        // Required for JPA entity
        this("", "", BigDecimal.ZERO, "");
    }
}
