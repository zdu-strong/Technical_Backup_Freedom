package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            return ResponseEntity.ok("Hello, World!");
        }), new ParameterizedTypeReference<LongTermTaskModel<String>>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertTrue(result.getBody().getIsDone());
        assertEquals("Hello, World!", result.getBody().getResult());
    }

}
