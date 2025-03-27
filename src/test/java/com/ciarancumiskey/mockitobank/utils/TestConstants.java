package com.ciarancumiskey.mockitobank.utils;

import org.junit.jupiter.params.provider.Arguments;
import org.testcontainers.utility.DockerImageName;

public class TestConstants {
    public static final String TEST_BIC = "IE94MOCK";
    public static final String IBAN_1 = "IE94MOCK12345612345678";
    public static final String UPDATED_IBAN_1 = "IE94MOCK23456712345678";
    public static final String IBAN_2 = "IE94MOCK65432187650987";
    public static final String IBAN_3 = "IE94MOCK06543208765098";
    public static final String IBAN_4 = "IE94MOCK01234512345678";
    public static final String IBAN_5 = "IE94MOCK01234512345670";
    public static final String IBAN_6 = "IE94MOCK65676702345678";
    public static final String IBAN_WO_EMAIL = "IE94MOCK65676702345679";
    public static final String IBAN_INVALID_EMAIL = "IE94MOCK65676722345679";
    public static final String IBAN_WHITESPACE_1 = "IE94MOCK65676732345679";
    public static final String IBAN_WHITESPACE_2 = "IE94MOCK65676632345679";
    public static final String IBAN_WHITESPACE_3 = "IE94MOCK65676642345679";
    public static final String IBAN_FAIL_1 = "IE94MOCK1234512345678";
    public static final String IBAN_FAIL_2 = "IE94MOCK123456712345678";
    public static final String IBAN_FAIL_3 = "IE94MOCK1234561234567";
    public static final String IBAN_FAIL_4 = "IE94MOCK123456123456789";
    public static final String IBAN_FAIL_5 = "IE94MOC12345612345678";
    public static final String IBAN_FAIL_6 = "IE94MCK12345612345678";
    public static final String IBAN_FAIL_7 = "IE94M0CK12345612345678";

    public static final String SORT_CODE_1 = "123456";
    public static final String SORT_CODE_2 = "654321";
    public static final String SORT_CODE_3 = "065432";
    public static final String SORT_CODE_4 = "012345";
    public static final String SORT_CODE_5 = "656767";
    public static final String SORT_CODE_6 = "656766";

    public static final String AC_NUMBER_1 = "12345678";
    public static final String AC_NUMBER_2 = "87650987";
    public static final String AC_NUMBER_3 = "08765098";
    public static final String AC_NUMBER_4 = "12345670";
    public static final String AC_NUMBER_5 = "02345678";
    public static final String AC_NUMBER_6 = "02345679";
    public static final String AC_NUMBER_7 = "22345679";
    private static final String AC_NUMBER_8 = "32345679";
    private static final String AC_NUMBER_9 = "42345679";

    public static final Arguments USER_1_ARGS = Arguments.of(SORT_CODE_1, "Joe Bloggs", "Joe Bloggs", AC_NUMBER_1,
            "jb@blahmail.com", "jb@blahmail.com", IBAN_1);
    public static final Arguments USER_2_ARGS = Arguments.of(SORT_CODE_2, "Jacqui Bloggs", "Jacqui Bloggs", AC_NUMBER_2,
            "jacqui.b@lorumipsum.com", "jacqui.b@lorumipsum.com", IBAN_2);
    public static final Arguments USER_3_ARGS = Arguments.of(SORT_CODE_3, "James Bloggs", "James Bloggs", AC_NUMBER_3,
            "jamieb99@email.com", "jamieb99@email.com", IBAN_3);
    public static final Arguments USER_4_ARGS = Arguments.of(SORT_CODE_4, "Jane Bloggs", "Jane Bloggs", AC_NUMBER_1,
            "janebloggs@lorummail.com", "janebloggs@lorummail.com", IBAN_4);
    public static final Arguments USER_5_ARGS = Arguments.of(SORT_CODE_4, "Anne-Marie Bloggs", "Anne-Marie Bloggs",
            AC_NUMBER_4, "am.bloggs@ipsummail.fr", "am.bloggs@ipsummail.fr", IBAN_5);
    public static final Arguments USER_6_ARGS = Arguments.of(SORT_CODE_5, "Jean-Paul Bloggs", "Jean-Paul Bloggs",
            AC_NUMBER_5, "jp.bloggs@blahpost.ca", "jp.bloggs@blahpost.ca", IBAN_6);
    // Try creating a user without an email address or with an improper email address
    public static final Arguments USER_WO_EMAIL_ARGS = Arguments.of(SORT_CODE_5, "John Smith", "John Smith",
            AC_NUMBER_6, "", "", IBAN_WO_EMAIL);
    public static final Arguments USER_INVALID_EMAIL_ARGS = Arguments.of(SORT_CODE_5, "Jeremy Usbourne    ",
            "Jeremy Usbourne", AC_NUMBER_7, "jez@lifecoaching", "", IBAN_INVALID_EMAIL);
    public static final Arguments USER_TRAILING_WHITESPACE_1 = Arguments.of(SORT_CODE_5, " Alan Johnson", "Alan Johnson",
            AC_NUMBER_8, "aj@consult.io", "aj@consult.io", IBAN_WHITESPACE_1);
    public static final Arguments USER_TRAILING_WHITESPACE_2 = Arguments.of(SORT_CODE_6, "Jeff Heaney ", "Jeff Heaney",
            AC_NUMBER_8, "jeff.heaney@jlbcredit.co.uk", "jeff.heaney@jlbcredit.co.uk", IBAN_WHITESPACE_2);
    public static final Arguments USER_TRAILING_WHITESPACE_3 = Arguments.of(SORT_CODE_6, "Mark Corrigan", "Mark Corrigan",
            AC_NUMBER_9, "mark.corrigan@jlbcredit.co.uk", "mark.corrigan@jlbcredit.co.uk", IBAN_WHITESPACE_3);

    // Updates
    public static final Arguments IBAN_1_JOSEPH = Arguments.of(TestConstants.IBAN_1, SORT_CODE_1, AC_NUMBER_1,
            "Joe Bloggs", "Joseph Bloggs", "Joseph Bloggs", "jb@blahmail.com", "jb@blahmail.com", "jb@blahmail.com");
    public static final Arguments IBAN_1_JBLOGGS_EMAIL = Arguments.of(TestConstants.IBAN_1, SORT_CODE_1, AC_NUMBER_1,
            "Joe Bloggs", "Joseph Bloggs", "Joseph Bloggs", "jb@blahmail.com", "j.bloggs@blahmail.com",
            "j.bloggs@blahmail.com");
    public static final Arguments IBAN_1_EMAIL_WHITESPACE = Arguments.of(TestConstants.IBAN_1, SORT_CODE_1, AC_NUMBER_1,
            "Joe Bloggs", "Joseph Bloggs", "Joseph Bloggs", "jb@blahmail.com", "j.bloggs@ipsummail.com ",
            "j.bloggs@ipsummail.com");
    public static final Arguments IBAN_1_EMAIL_TABS = Arguments.of(TestConstants.IBAN_1, SORT_CODE_1, AC_NUMBER_1,
            "Joe Bloggs", "Joseph Bloggs", "Joseph Bloggs", "jb@blahmail.com", "j.bloggs@neutronmail.com\t\t",
            "j.bloggs@neutronmail.com");
    public static final Arguments IBAN_1_EMAIL_NEWLINES = Arguments.of(TestConstants.IBAN_1, SORT_CODE_1, AC_NUMBER_1,
            "Joe Bloggs", "Joseph Bloggs", "Joseph Bloggs", "jb@blahmail.com", "j.bloggs@electronmail.ch\n\n",
            "j.bloggs@electronmail.ch");
    public static final Arguments IBAN_WHITESPACE_1_NEW_EMAIL = Arguments.of(IBAN_WHITESPACE_1, SORT_CODE_5, AC_NUMBER_8,
            "Alan Johnson", " Alan Johnson\t", "Alan Johnson", "aj@consult.io", "a.johnson@jlbcredit.co.uk\n\n",
            "a.johnson@jlbcredit.co.uk");

    // Deletions
    public static final Arguments DELETE_USER_1_ARGS = Arguments.of(IBAN_1, SORT_CODE_1, AC_NUMBER_1, "Joe Bloggs",
            "jb@blahmail.com");
    public static final Arguments DELETE_USER_2_ARGS = Arguments.of(IBAN_2, SORT_CODE_2, AC_NUMBER_2, "Jacqui Bloggs",
            "jacqui.b@lorumipsum.com");
    public static final Arguments DELETE_USER_3_ARGS = Arguments.of(IBAN_3, SORT_CODE_3, AC_NUMBER_3, "James Bloggs",
            "jamieb99@email.com");
    public static final Arguments DELETE_USER_4_ARGS = Arguments.of(IBAN_4, SORT_CODE_4, AC_NUMBER_1, "Jane Bloggs",
            "janebloggs@lorummail.com");
    public static final Arguments DELETE_USER_5_ARGS = Arguments.of(IBAN_5, SORT_CODE_4, AC_NUMBER_4, "Anne-Marie Bloggs",
             "am.bloggs@ipsummail.fr");

    // Error cases
    public static final Arguments USER_INVALID_SORT_CODE_5_NUMS = Arguments.of("12345", "Test 1", AC_NUMBER_1, "johnjackson@email.com", Constants.ERROR_MSG_INVALID_SORT_CODE.formatted("12345"));
    public static final Arguments USER_INVALID_SORT_CODE_7_NUMS = Arguments.of("1234567", "Test 2", AC_NUMBER_2, "jackjohnson@email.com", Constants.ERROR_MSG_INVALID_SORT_CODE.formatted("1234567"));
    public static final Arguments USER_INVALID_AC_NUMBER_7_NUMS = Arguments.of(SORT_CODE_1, "Test 3", "1234567", "maire.ni.cheilleagh@email.ie", Constants.ERROR_MSG_INVALID_AC_NUMBER.formatted("1234567"));
    public static final Arguments USER_INVALID_AC_NUMBER_9_NUMS = Arguments.of(SORT_CODE_1, "Test 4", "123456789", "", Constants.ERROR_MSG_INVALID_AC_NUMBER.formatted("123456789"));
    public static final Arguments USER_INVALID_NO_NAME = Arguments.of(SORT_CODE_1, "", AC_NUMBER_1, "", Constants.ERROR_MSG_BLANK_AC_NAME);
    public static final Arguments USER_INVALID_WHITESPACE_NAME = Arguments.of(SORT_CODE_1, "     ", AC_NUMBER_1, "", Constants.ERROR_MSG_BLANK_AC_NAME);
    public static final Arguments USER_INVALID_TABS_NAME = Arguments.of(SORT_CODE_1, "\t\t\t", AC_NUMBER_1, "", Constants.ERROR_MSG_BLANK_AC_NAME);
    public static final Arguments USER_INVALID_NEWLINE_NAME = Arguments.of(SORT_CODE_1, "\n\n", AC_NUMBER_1, "", Constants.ERROR_MSG_BLANK_AC_NAME);

    // Testcontainers
    public static final DockerImageName MYSQL_80_IMAGE_NAME = DockerImageName.parse("mysql:8.0.36");
}
