package com.ciarancumiskey.mockitobank.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (final JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
