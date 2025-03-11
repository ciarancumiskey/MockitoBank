package com.ciarancumiskey.mockitobank.utils;

import java.util.regex.Pattern;

public class Constants {
    public final static String MOCKITO_BANK_BIC = "MOCKIE94";
    public final static String MOCKITO_BANK_IBAN_PREFIX = "IE94MOCK";

    // Controllers
    public final static String ACCOUNT_PATH = "/accounts";
    public final static String REGISTRATION_PATH = "/register";
    public final static String LOAD_ACCOUNT_PATH = "/load/{accountIban}";
    public final static String UPDATE_ACCOUNT_PATH = "/update";

    // Regex
    public final static Pattern SORT_CODE_REGEX = Pattern.compile("[0-9]{6}");
    public final static Pattern ACCOUNT_NUMBER_REGEX = Pattern.compile("[0-9]{8}");
    public final static Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$");

    // Error messages
    public static final String ERROR_MSG_BLANK_AC_NAME = "Name cannot be blank.";
    public static final String ERROR_MSG_IBAN_NOT_FOUND = "No account found with IBAN %s";
    public static final String ERROR_MSG_INVALID_AC_NUMBER = "Account number %s is invalid, it must be 8 numbers and nothing else.";
    public static final String ERROR_MSG_INVALID_IBAN = "Invalid IBAN provided. IBAN must be 22 characters long, starting with the bank's BIC.";
    public static final String ERROR_MSG_INVALID_SORT_CODE = "Sort code %s is invalid, it must be 6 numbers and nothing else.";
}
