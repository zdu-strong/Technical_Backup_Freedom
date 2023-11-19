package com.springboot.project.test.common.longtermtask;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.springboot.project.test.BaseTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;

public class LongTermTaskUtilTest extends BaseTest {

    @Test
    public void test() throws URISyntaxException {
        var relativeUrl = this.fromLongTermTask(() -> {
            return this.longTermTaskUtil.run(() -> {
                return ResponseEntity.ok().build();
            }).getBody();
        });
        var url = new URIBuilder(relativeUrl).build();
        var result = this.testRestTemplate.getForEntity(url, JsonNode.class);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

}
