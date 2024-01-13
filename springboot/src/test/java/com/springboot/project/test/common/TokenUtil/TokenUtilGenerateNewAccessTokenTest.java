package com.springboot.project.test.common.TokenUtil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class TokenUtilGenerateNewAccessTokenTest extends BaseTest {
    private UserModel user;

    @Test
    public void test() {
        var accessToken = this.tokenUtil.generateNewAccessToken(this.user.getAccessToken());
        assertTrue(StringUtils.isNotBlank(accessToken));
        assertNotNull(this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getId());
        assertEquals(this.user.getId(),
                this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getSubject());
        assertNotNull(this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getIssuedAt());
        assertNotEquals(this.tokenUtil.getDecodedJWTOfAccessToken(this.user.getAccessToken()).getId(),
                this.tokenUtil.getDecodedJWTOfAccessToken(accessToken).getId());
    }

    @BeforeEach
    public void beforeEach() {
        this.user = this.createAccount("zdu.strong@gmail.com");
    }
}
