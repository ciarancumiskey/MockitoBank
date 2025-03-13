package com.ciarancumiskey.mockitobank.models;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
public class TransactionResponse {
    // contains updated balances of payee and payer (if applicable)
    private final Map<String, BigDecimal> updatedAccountBalances = new HashMap<>();

    public void updatePayeeBalance(final BigDecimal updatedBalance) {
        this.updatedAccountBalances.put("payee", updatedBalance);
    }

    public void updatePayerBalance(final BigDecimal updatedBalance) {
        this.updatedAccountBalances.put("payer", updatedBalance);
    }
}
