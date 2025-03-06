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
public class AccountCreationRequest {
    @NonNull
    private String sortCode;
    @NonNull private String accountName;
    @NonNull private String accountNumber;
    private String emailAddress;

}
