package com.springboot.project.test.scheduled.StorageSpaceScheduled;

import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;

public class StorageSpaceScheduledCleanDiskStorageTest extends BaseTest {

    @Test
    public void test() {
        this.storageSpaceScheduled.cleanDiskStorage();
    }

}
