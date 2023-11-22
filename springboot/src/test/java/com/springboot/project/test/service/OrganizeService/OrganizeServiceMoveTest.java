package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.BaseTest;

public class OrganizeServiceMoveTest extends BaseTest {

    private String organizeId;
    private String parentOrganizeId;

    @Test
    public void test() {
        this.organizeService.move(organizeId, parentOrganizeId);
        var result = this.organizeService.getById(organizeId);
        assertEquals(this.parentOrganizeId, result.getParent().getId());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
            var childOrganize = this.organizeService.create(childOrganizeModel);
            this.organizeId = childOrganize.getId();
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            this.parentOrganizeId = parentOrganize.getId();
        }
    }

}
