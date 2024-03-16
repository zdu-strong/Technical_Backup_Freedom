package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;

import org.junit.jupiter.api.Test;

public class StorageSpaceServiceIsUsedTest extends BaseTest {
    private String folderName = Generators.timeBasedReorderedGenerator().generate().toString();

    @Test
    public void test() {
        var isUsed = this.storageSpaceService.isUsed(folderName);
        assertTrue(isUsed);
    }
}
