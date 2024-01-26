package com.springboot.project.test.service.OrganizeService;

import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceCheckExistOrganizeAllowEmptyFromNullTest extends BaseTest {

    @Test
    public void test() {
        this.organizeService.checkExistOrganizeAllowEmpty(null);
    }

}
