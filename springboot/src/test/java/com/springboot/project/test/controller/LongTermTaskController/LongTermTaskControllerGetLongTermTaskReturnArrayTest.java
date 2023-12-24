package com.springboot.project.test.controller.LongTermTaskController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.google.common.collect.Lists;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;

public class LongTermTaskControllerGetLongTermTaskReturnArrayTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            var httpHeaders = new HttpHeaders();
            httpHeaders.addAll("MyCustomHeader", Lists.newArrayList("Hello, World!"));
            httpHeaders.set("MySecondCustomHeader", "Hello, World!");
            return ResponseEntity.ok().headers(httpHeaders).body(new String[] { "Hello, World!", "I love girl" });
        }), new ParameterizedTypeReference<LongTermTaskModel<String[]>>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertTrue(result.getBody().getIsDone());
        assertEquals(2, result.getBody().getResult().length);
        assertEquals("Hello, World!", result.getBody().getResult()[0]);
        assertEquals("I love girl", result.getBody().getResult()[1]);
        assertEquals(result.getHeaders().get("MyCustomHeader").size(), 1);
        assertEquals(result.getHeaders().get("MyCustomHeader").get(0), "Hello, World!");
        assertEquals(result.getHeaders().get("MySecondCustomHeader").get(0), "Hello, World!");
    }

}
