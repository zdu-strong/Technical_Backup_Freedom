package com.springboot.project.test.common.PermissionUtil;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import com.springboot.project.test.common.BaseTest.BaseTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;

public class PermissionUtilCheckIsSignInFromAccessTokenNotSignInTest extends BaseTest {
    private String accessToken;

    @Test
    public void test() {
        assertThrows(ResponseStatusException.class, () -> {
            this.permissionUtil.checkIsSignIn(this.accessToken);
        });
    }

    @BeforeEach
    public void beforeEach() {
        this.accessToken = this.tokenService.getAccessToken(request);
    }
}
