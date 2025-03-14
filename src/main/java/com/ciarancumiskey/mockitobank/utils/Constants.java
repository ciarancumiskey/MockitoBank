package com.ciarancumiskey.mockitobank.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Constants {
    // Allows LocalDateTime to be returned in response bodies
    public final static Gson g = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();

    // Controllers
    public final static String ACCOUNT_PATH = "/accounts";
    public final static String REGISTRATION_PATH = "/register";
    public final static String LOAD_ACCOUNT_PATH = "/load/{accountIban}";
    public final static String UPDATE_ACCOUNT_PATH = "/update";
    public static final String DELETE_PATH = "/delete/{accountIban}";
    public static final String TRANSACTIONS_PATH = "/transactions";
    public static final String TRANSFER_PATH = "/transfer";
    public static final String HISTORY_PATH = "/history/{accountIban}";

    // Regex
    public final static Pattern SORT_CODE_REGEX = Pattern.compile("[0-9]{6}");
    public final static Pattern ACCOUNT_NUMBER_REGEX = Pattern.compile("[0-9]{8}");
    public final static Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$");

    // Error messages
    public static final String ERROR_MSG_BLANK_AC_NAME = "Name cannot be blank.";
    public static final String ERROR_MSG_DUPLICATE_IBAN = "Account with IBAN %s already exists";
    public static final String ERROR_MSG_IBAN_NOT_FOUND = "No account found with IBAN %s";
    public static final String ERROR_MSG_INVALID_AC_NUMBER = "Account number %s is invalid, it must be 8 numbers and nothing else.";
    public static final String ERROR_MSG_INVALID_IBAN = "Invalid IBAN provided. IBAN must be 22 characters long, starting with the bank's BIC.";
    public static final String ERROR_MSG_INVALID_SORT_CODE = "Sort code %s is invalid, it must be 6 numbers and nothing else.";
    public static final String ERROR_MSG_NOT_ENOUGH_MONEY = "Not enough money in account.";
    public static final String ERROR_MSG_PAYEE_NOT_FOUND = "Unable to find payee with IBAN %s";
    public static final String ERROR_MSG_PAYER_NOT_FOUND = "Unable to find payer with IBAN %s";

    // Other response messages
    public static final String MSG_DELETION_SUCCESSFUL = "Account with IBAN %s deleted successfully.";
}
