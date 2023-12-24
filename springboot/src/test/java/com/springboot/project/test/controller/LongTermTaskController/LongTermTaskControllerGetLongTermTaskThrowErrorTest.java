package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertThrows;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskThrowErrorTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        assertThrows(RuntimeException.class, () -> {
            this.fromLongTermTask(() -> {
                return this.longTermTaskUtil.run(() -> {
                    throw new RuntimeException("Failed due to insufficient funds");
                }).getBody();
            }, new ParameterizedTypeReference<LongTermTaskModel<String>>() {
            });
        });
    }

}
