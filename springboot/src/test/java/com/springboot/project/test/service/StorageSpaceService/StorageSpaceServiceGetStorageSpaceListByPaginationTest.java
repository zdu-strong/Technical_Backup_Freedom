package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;
import com.springboot.project.test.common.BaseTest.BaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StorageSpaceServiceGetStorageSpaceListByPaginationTest extends BaseTest {
    private String folderName = Generators.timeBasedGenerator().generate().toString();

    @Test
    public void test() throws URISyntaxException {
        var result = this.storageSpaceService.getStorageSpaceListByPagination(1L, 1L);
        assertEquals(1, result.getPageNum());
        assertEquals(1, result.getPageSize());
        assertEquals(1, result.getTotalRecord());
        assertEquals(1, result.getTotalPage());
        assertEquals(1, result.getList().size());
    }

    @BeforeEach
    public void beforeEach() {
        this.storageSpaceService.isUsed(folderName);
    }

    @AfterEach
    public void afterEach() {
        this.storageSpaceService.deleteStorageSpaceEntity(folderName);
    }
}
