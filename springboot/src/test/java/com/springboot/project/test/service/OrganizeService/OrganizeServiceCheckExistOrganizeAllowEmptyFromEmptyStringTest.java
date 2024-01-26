package com.springboot.project.test.service.OrganizeService;

import org.junit.jupiter.api.Test;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class OrganizeServiceCheckExistOrganizeAllowEmptyFromEmptyStringTest extends BaseTest {

    @Test
    public void test() {
        this.organizeService.checkExistOrganizeAllowEmpty("   \n");
    }

}
