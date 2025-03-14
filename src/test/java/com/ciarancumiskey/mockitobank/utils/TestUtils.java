package com.ciarancumiskey.mockitobank.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.ciarancumiskey.mockitobank.utils.Constants.g;

@Slf4j
public class TestUtils {
    public static String asJsonString(final Object obj) {
        return g.toJson(obj);
    }

    public static Object fromJsonString(String accountJsonString, Class<?> targetClass) {
        return g.fromJson(accountJsonString, targetClass);
    }

    public static MvcResult sendDeleteRequest(final MockMvc mvc, final String endpoint,
                                           final ResultMatcher expectedStatusCode) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.delete(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(expectedStatusCode)
                        .andReturn();
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
