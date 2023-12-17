package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerGetAccountForSignInTest extends BaseTest {
    private UserModel user;
    private String email;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(this.user.getId(), response.getBody().getId());
        assertTrue(StringUtils.isNotBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getPrivateKeyOfRSA()));
        assertTrue(StringUtils.isBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getPublicKeyOfRSA()));
        assertNull(response.getBody().getUserEmailList());
        assertNull(response.getBody().getCreateDate());
        assertNull(response.getBody().getUpdateDate());
        assertTrue(StringUtils.isBlank(response.getBody().getAccessToken()));

    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.user = this.createAccount(email);
    }

}
