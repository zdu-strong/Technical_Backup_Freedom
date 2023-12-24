package com.springboot.project.test.common.longtermtask;

import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.springboot.project.model.LongTermTaskModel;
import com.springboot.project.test.BaseTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URISyntaxException;

public class LongTermTaskUtilTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var result = this.fromLongTermTask(() -> this.longTermTaskUtil.run(() -> {
            return ResponseEntity.ok().build();
        }), new ParameterizedTypeReference<LongTermTaskModel<Void>>() {
        });
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody().getId());
        assertEquals(36, result.getBody().getId().length());
        assertTrue(result.getBody().getIsDone());
        assertNotNull(result.getBody().getCreateDate());
        assertNotNull(result.getBody().getUpdateDate());
        assertNull(result.getBody().getResult());
    }

}
