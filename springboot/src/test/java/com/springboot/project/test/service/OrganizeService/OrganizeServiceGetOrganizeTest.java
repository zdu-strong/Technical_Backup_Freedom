package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceGetOrganizeTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var result = this.organizeService.getById(organizeId);
        assertNotNull(result.getId());
        assertEquals(this.organizeId, result.getId());
        assertEquals("Super Saiyan Son Goku", result.getName());
        assertEquals(0, result.getChildList().size());
        assertNull(result.getParent());
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
    }

}
