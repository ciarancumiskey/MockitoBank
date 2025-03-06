package com.ciarancumiskey.mockitobank.utils;

import java.util.regex.Pattern;

public class Constants {
    public final static String MOCKITO_BANK_BIC = "MOCKIE94";
    public final static String MOCKITO_BANK_IBAN_PREFIX = "IE94MOCK";

    // Controllers
    public final static String ACCOUNT_PATH = "/accounts";
    public final static String REGISTRATION_PATH = "/register";

    // Regex
    public final static Pattern SORT_CODE_REGEX = Pattern.compile("[0-9]{6}");
    public final static Pattern ACCOUNT_NUMBER_REGEX = Pattern.compile("[0-9]{8}");
    public final static Pattern EMAIL_REGEX = Pattern.compile("^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$");
}
