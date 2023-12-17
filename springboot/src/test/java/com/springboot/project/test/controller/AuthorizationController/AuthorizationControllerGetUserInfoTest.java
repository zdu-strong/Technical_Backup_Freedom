package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerGetUserInfoTest extends BaseTest {

    private UserModel user;
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/get_user_info").build();
        var response = this.testRestTemplate.getForEntity(url, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.user.getId(), response.getBody().getId());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPrivateKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPublicKeyOfRSA()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getAccessToken()));
        assertNotNull(response.getBody().getCreateDate());
        assertNotNull(response.getBody().getUpdateDate());

        assertEquals(1, response.getBody().getUserEmailList().size());
        assertEquals(this.email,
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getEmail()).getOnlyValue());
        assertTrue(StringUtils.isNotBlank(
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getId()).getOnlyValue()));
        assertNull(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getVerificationCodeEmail()).getOnlyValue());
        assertTrue(StringUtils.isNotBlank(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getUser().getId()).getOnlyValue()));
        assertNotEquals(this.user.getPrivateKeyOfRSA(), response.getBody().getPrivateKeyOfRSA());
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
    }

}
