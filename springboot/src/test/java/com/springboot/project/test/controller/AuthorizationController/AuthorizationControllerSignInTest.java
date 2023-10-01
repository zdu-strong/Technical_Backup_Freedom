package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignInTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException,
            JsonMappingException, JsonProcessingException {
        var result = this.signIn(this.email, this.email);
        assertNotNull(result);
        assertTrue(StringUtils.isNotBlank(result.getId()));
        assertTrue(StringUtils.isNotBlank(result.getUsername()));
        assertTrue(StringUtils.isNotBlank(result.getAccess_token()));
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
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(this.email);
    }

}
