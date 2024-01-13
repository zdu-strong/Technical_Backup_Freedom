package com.springboot.project.test.controller.OrganizeController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.net.URISyntaxException;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeControllerUpdateOrganizeTest extends BaseTest {

    private OrganizeModel organizeModel;

    @Test
    public void test() throws URISyntaxException {
        var url = new URIBuilder("/organize").build();
        var response = this.testRestTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(organizeModel),
                Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var result = this.organizeService.getById(organizeModel.getId());
        assertNotNull(result.getId());
        assertEquals(36, result.getId().length());
        assertEquals("Piccolo", result.getName());
        assertEquals(0, result.getLevel());
        assertNotNull(result.getCreateDate());
        assertNotNull(result.getUpdateDate());
        assertEquals(0, result.getChildCount());
        assertEquals(0, result.getDescendantCount());
        assertNull(result.getParent());
        assertEquals(0, result.getChildList().size());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeModel = this.organizeService.create(organizeModel);
        this.organizeModel.setName("Piccolo");
    }
}
