package com.ciarancumiskey.mockitobank.utils;

import com.google.gson.Gson;

public class TestUtils {
    private static final Gson gson = new Gson();
    public static String asJsonString(final Object obj) {
        return gson.toJson(obj);
    }

    public static Object fromJsonString(String accountJsonString, Class<?> targetClass) {
        return gson.fromJson(accountJsonString, targetClass);
    }
}
