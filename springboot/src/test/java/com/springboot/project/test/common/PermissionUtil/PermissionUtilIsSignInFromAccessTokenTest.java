package com.springboot.project.test.common.PermissionUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class PermissionUtilIsSignInFromAccessTokenTest extends BaseTest {
    private String accessToken;

    @Test
    public void test() {
        var isSignIn = this.permissionUtil.isSignIn(this.accessToken);
        assertTrue(isSignIn);
    }

    @BeforeEach
    public void beforeEach() {
        var user = this.createAccount("zdu.strong@gmail.com");
        this.accessToken = user.getAccessToken();
    }
}
