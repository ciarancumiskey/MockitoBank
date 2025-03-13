package com.ciarancumiskey.mockitobank.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private final String payeeAccount; // can be null to denote ATM withdrawal
    private final String payerAccount; // can be null to denote ATM deposit
    private final BigDecimal amount;

    public Transaction(final String payee, final String payer, final BigDecimal amount){
        this.payeeAccount = payee;
        this.payerAccount = payer;
        this.amount = amount;
    }
}
