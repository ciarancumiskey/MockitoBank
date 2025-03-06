package com.ciarancumiskey.mockitobank.utils;

import org.junit.jupiter.params.provider.Arguments;

public class TestConstants {
    public static final Arguments USER_1_ARGS = Arguments.of("123456", "Joe Bloggs", "12345678", "jb@blahmail.com", "IE94MOCK12345612345678");
    public static final Arguments USER_2_ARGS = Arguments.of("654321", "Jacqui Bloggs", "87650987", "jacqui.b@lorumipsum.com", "IE94MOCK65432187650987");
    public static final Arguments USER_3_ARGS = Arguments.of("065432", "James Bloggs", "08765098", "jamieb99@email.com", "IE94MOCK06543208765098");
    public static final Arguments USER_4_ARGS = Arguments.of("012345", "Jane Bloggs", "12345678", "janebloggs@lorummail.com", "IE94MOCK01234512345678");
    public static final Arguments USER_5_ARGS = Arguments.of("012345", "Anne-Marie Bloggs", "12345670", "am.bloggs@ipsummail.fr", "IE94MOCK01234512345670");
    public static final Arguments USER_6_ARGS = Arguments.of("656767", "Jean-Paul Bloggs", "02345678", "jp.bloggs@blahpost.ca","IE94MOCK65676702345678");
}
