package com.springboot.project.test.controller.AuthorizationController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class AuthorizationControllerSignInTest extends BaseTest {
    private String email;
    private UserModel user;

    @Test
    public void test() throws URISyntaxException, InvalidKeySpecException, NoSuchAlgorithmException,
            JsonMappingException, JsonProcessingException {
        var passwordParameter = this.encryptDecryptService.encryptByPrivateKeyOfRSA(
                new ObjectMapper().writeValueAsString(
                        new UserModel().setCreateDate(new Date()).setPrivateKeyOfRSA("Private Key")),
                this.encryptDecryptService.decryptByAES(this.user.getPrivateKeyOfRSA(),
                        this.encryptDecryptService.generateSecretKeyOfAES(this.email)));
        var url = new URIBuilder("/sign_in").setParameter("userId", user.getId())
                .setParameter("password", passwordParameter)
                .build();
        var response = this.testRestTemplate.postForEntity(url, null, String.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody()));
    }

    @BeforeEach
    public void beforeEach() throws InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
        this.createAccount(this.email);
        this.user = getAccount(this.email);
    }

    private UserModel getAccount(String email) throws URISyntaxException {
        var url = new URIBuilder("/sign_in/get_account").setParameter("userId", email).build();
        var response = this.testRestTemplate.postForEntity(url, null, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var user = response.getBody();
        return user;
    }

}
