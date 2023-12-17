package com.springboot.project.test.common.TokenUtil;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import com.springboot.project.test.BaseTest;

public class TokenUtilGetAccessTokenTest extends BaseTest {

    @Test
    public void test() {
        var accessToken = this.tokenUtil.getAccessToken(this.request);
        assertTrue(StringUtils.isNotBlank(accessToken));
    }

    @BeforeEach
    public void beforeEach() {
        var user = this.createAccount("zdu.strong@gmail.com");
        this.request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + user.getAccessToken());
    }
}
