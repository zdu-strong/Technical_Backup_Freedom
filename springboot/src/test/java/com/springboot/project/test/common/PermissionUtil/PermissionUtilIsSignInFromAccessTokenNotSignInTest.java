package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class PermissionUtilIsSignInFromAccessTokenNotSignInTest extends BaseTest {
    private String accessToken;

    @Test
    public void test() {
        var isSignIn = this.permissionUtil.isSignIn(this.accessToken);
        assertFalse(isSignIn);
    }

    @BeforeEach
    public void beforeEach() {
        this.accessToken = this.tokenService.getAccessToken(this.request);
    }
}
