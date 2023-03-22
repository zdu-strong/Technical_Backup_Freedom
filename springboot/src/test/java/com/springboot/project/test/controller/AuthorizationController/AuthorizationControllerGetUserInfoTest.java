package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
import com.springboot.project.model.TokenModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerGetUserInfoTest extends BaseTest {
    private TokenModel tokenModel;
    private String email;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException {
        var url = new URIBuilder("/get_user_info").build();
        var response = this.testRestTemplate.getForEntity(url, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.tokenModel.getUserModel().getId(), response.getBody().getId());
        assertTrue(StringUtils.isNotBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getEmail()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPrivateKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPublicKeyOfRSA()));
        assertTrue(response.getBody().getHasRegistered());
        assertEquals(1, response.getBody().getUserEmailList().size());
        assertEquals(this.email,
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getEmail()).getOnlyValue());
        assertTrue(StringUtils.isNotBlank(
                JinqStream.from(response.getBody().getUserEmailList()).select(s -> s.getId()).getOnlyValue()));
        assertTrue(StringUtils.isBlank(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getVerificationCode()).getOnlyValue()));
        assertTrue(StringUtils.isNotBlank(JinqStream.from(response.getBody().getUserEmailList())
                .select(s -> s.getUser().getId()).getOnlyValue()));
        assertNotEquals(this.tokenModel.getRSA().getPrivateKeyBase64(), response.getBody().getPrivateKeyOfRSA());
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.tokenModel = this.createAccount(email);
    }

}