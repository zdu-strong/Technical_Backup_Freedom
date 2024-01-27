package com.springboot.project.test.service.OrganizeClosureService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeClosureServiceUpdateTest extends BaseTest {

    private String parentOrganizeId;
    private String childOrganizeId;

    @Test
    public void test() {
        this.organizeClosureService.update(this.childOrganizeId);
        var result = this.organizeService.searchByName(1L, 20L, "Gohan", this.parentOrganizeId);
        assertEquals(1, result.getTotalRecord());
        assertEquals(this.childOrganizeId, JinqStream.from(result.getList()).select(s -> s.getId()).getOnlyValue());
        assertEquals(this.parentOrganizeId,
                JinqStream.from(result.getList()).select(s -> s.getParent().getId()).getOnlyValue());
        assertEquals("Son Gohan",
                JinqStream.from(result.getList()).select(s -> s.getName()).getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() {
        {
            var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
            var childOrganize = this.organizeService.create(childOrganizeModel);
            this.childOrganizeId = childOrganize.getId();
        }
        {
            var parentOrganizeModel = new OrganizeModel().setName("Piccolo");
            var parentOrganize = this.organizeService.create(parentOrganizeModel);
            this.parentOrganizeId = parentOrganize.getId();
        }
        this.organizeService.move(this.childOrganizeId, this.parentOrganizeId);
        var pagination = this.organizeService.searchByName(1L, 20L, "Son Gohan", this.parentOrganizeId);
        assertEquals(0, pagination.getTotalRecord());
    }

    @AfterEach
    public void afterEach() {
        this.organizeClosureRefreshScheduled.refresh();
    }

}
