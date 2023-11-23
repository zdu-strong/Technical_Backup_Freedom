package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.BaseTest;

public class OrganizeControllerDeleteOrganizeNotExistOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/organize").setParameter("id", this.organizeId)
                .build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(null), Throwable.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Organize does not exist", response.getBody().getMessage());
    }

    @BeforeEach
    public void beforeEach() {
        this.organizeId = Generators.timeBasedGenerator().generate().toString();
    }
}
