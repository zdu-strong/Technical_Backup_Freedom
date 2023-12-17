package com.springboot.project.test.controller.UserController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;

public class UserControllerGetUserByIdTest extends BaseTest {

    private String userId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/user").setParameter("id", userId).build();
        var response = this.testRestTemplate.getForEntity(url, UserModel.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(StringUtils.isNotBlank(response.getBody().getId()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getUsername()));
        assertTrue(StringUtils.isBlank(response.getBody().getAccessToken()));
        assertTrue(StringUtils.isBlank(response.getBody().getPassword()));
        assertTrue(StringUtils.isBlank(response.getBody().getPrivateKeyOfRSA()));
        assertTrue(StringUtils.isNotBlank(response.getBody().getPublicKeyOfRSA()));
        assertNotNull(response.getBody().getCreateDate());
        assertNotNull(response.getBody().getUpdateDate());
        assertEquals(0, response.getBody().getUserEmailList().size());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.userId = this.createAccount("zdu.strong@gmail.com").getId();
    }

}
