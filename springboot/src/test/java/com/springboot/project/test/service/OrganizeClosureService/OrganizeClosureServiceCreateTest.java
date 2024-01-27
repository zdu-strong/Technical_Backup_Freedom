package com.springboot.project.test.service.OrganizeClosureService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeClosureServiceCreateTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {

        assertThrows(DataIntegrityViolationException.class, () -> {
            this.organizeClosureService.create(organizeId);
        });
    }

    @BeforeEach
    public void beforeEach() {
        var organizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        this.organizeId = this.organizeService.create(organizeModel).getId();
    }

}
