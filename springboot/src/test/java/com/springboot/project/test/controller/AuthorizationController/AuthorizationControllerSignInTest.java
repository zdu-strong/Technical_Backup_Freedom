package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignInTest extends BaseTest {
    private String email;

    @Test
    public void test() {
        var result = this.createAccount(this.email);
        assertNotNull(result);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getUsername()));
        assertTrue(StringUtils.isNotBlank(result.getAccessToken()));
        assertTrue(StringUtils.isBlank(result.getPassword()));
        assertTrue(StringUtils.isNotBlank(result.getPrivateKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(result.getPublicKeyOfRSA()));
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(1, result.getUserEmailList().size());
        assertTrue(StringUtils
                .isNotBlank(JinqStream.from(result.getUserEmailList()).select(s -> s.getEmail()).getOnlyValue()));
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
