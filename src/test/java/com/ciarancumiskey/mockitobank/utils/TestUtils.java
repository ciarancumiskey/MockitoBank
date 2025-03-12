package com.ciarancumiskey.mockitobank.utils;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@Slf4j
public class TestUtils {
    private static final Gson gson = new Gson();
    public static String asJsonString(final Object obj) {
        return gson.toJson(obj);
    }

    public static Object fromJsonString(String accountJsonString, Class<?> targetClass) {
        return gson.fromJson(accountJsonString, targetClass);
    }

    public static MvcResult sendGetRequest(final MockMvc mvc, final String endpoint,
                                           final ResultMatcher expectedStatusCode) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.get(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(expectedStatusCode)
                        .andReturn();
    }

    public static MvcResult sendPostRequest(final MockMvc mvc, final String endpoint, final String requestContent,
                                            final ResultMatcher expectedStatusCode) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                        .andExpect(expectedStatusCode)
                        .andReturn();
    }

    public static MvcResult sendPutRequest(final MockMvc mvc, final String endpoint, final String requestContent,
                                            final ResultMatcher expectedStatusCode) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.put(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestContent))
                        .andExpect(expectedStatusCode)
                        .andReturn();
    }
}
