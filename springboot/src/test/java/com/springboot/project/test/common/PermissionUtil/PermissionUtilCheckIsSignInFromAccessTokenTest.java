package com.springboot.project.test.common.PermissionUtil;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class PermissionUtilCheckIsSignInFromAccessTokenTest extends BaseTest {
    private String accessToken;

    @Test
    public void test() {
        this.permissionUtil.checkIsSignIn(this.accessToken);
    }

    @BeforeEach
    public void beforeEach() {
        var user = this.createAccount("zdu.strong@gmail.com");
        this.accessToken = user.getAccessToken();
    }
}
