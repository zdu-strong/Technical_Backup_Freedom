package com.springboot.project.test.service.OrganizeService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceGetOrganizeListThatContainsDeletedTest extends BaseTest {

    private String organizeId;

    @Test
    public void test() {
        var list = this.organizeService.getOrganizeListThatContainsDeleted(1L, Long.MAX_VALUE).getList();
        var result = JinqStream.from(list).where(s -> s.getId().equals(this.organizeId)).getOnlyValue();
        assertNotNull(result.getId());
        assertEquals(this.organizeId, result.getId());
        assertEquals("Super Saiyan Son Goku", result.getName());
        assertEquals(0, result.getChildList().size());
        assertEquals(0, result.getChildCount());
        assertNull(result.getParent());
        assertEquals(0, result.getLevel());
    }

    @BeforeEach
    public void beforeEach() {

        var parentOrganizeModel = new OrganizeModel().setName("Super Saiyan Son Goku");
        var parentOrganize = this.organizeService.create(parentOrganizeModel);
        var childOrganizeModel = new OrganizeModel().setName("Son Gohan").setParent(parentOrganize);
        this.organizeService.create(childOrganizeModel);
        this.organizeId = parentOrganize.getId();
        this.organizeService.delete(this.organizeId);
    }

}
