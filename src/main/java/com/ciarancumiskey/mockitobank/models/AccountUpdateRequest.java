package com.ciarancumiskey.mockitobank.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AccountUpdateRequest {
    @NonNull private String accountIban;
    private String accountName;
    private String emailAddress;
}
