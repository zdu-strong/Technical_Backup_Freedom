package com.springboot.project.test.service.UserEmailService;

import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class UserEmailServiceCheckEmailIsNotUsedTest extends BaseTest {
    private String email;

    @Test
    public void test() throws URISyntaxException {
        this.userEmailService.checkEmailIsNotUsed(this.email);
    }

    @BeforeEach
    public void beforeEach() {
        this.email = Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com";
    }

}
