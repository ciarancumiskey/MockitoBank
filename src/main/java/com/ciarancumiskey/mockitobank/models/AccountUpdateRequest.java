package com.ciarancumiskey.mockitobank.models;

import jakarta.validation.constraints.Size;
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
    @NonNull @Size(min = 22, max = 22) private String accountIban;
    private String accountName;
    private String emailAddress;
}
