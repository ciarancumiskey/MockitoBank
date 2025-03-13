package com.ciarancumiskey.mockitobank.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class TransactionRequest {
    private final TransactionType transactionType;
    private final String payee;
    private final String payer;
    @NonNull private final BigDecimal amount;
}
