package com.ekagra.auth.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailUtilsTest {

    @Test
    void testValidEmailDomain() {
        EmailUtils utils = new EmailUtils();
        assertTrue(utils.isDomainValid("test@gmail12.com"));
    }

    @Test
    void testInvalidEmailDomain() {
        EmailUtils utils = new EmailUtils();
        assertFalse(utils.isDomainValid("test@fakebogusdomainxyz123.com"));
    }
}

