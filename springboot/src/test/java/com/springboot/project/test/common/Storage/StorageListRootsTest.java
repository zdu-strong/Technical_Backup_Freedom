package com.springboot.project.test.common.Storage;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.springboot.project.test.common.BaseTest.BaseTest;

public class StorageListRootsTest extends BaseTest {
    private String folderNameOfResource;

    @Test
    public void test() throws IOException, URISyntaxException {
        var list = this.storage.listRoots().filter(s -> s.equals(folderNameOfResource)).toList().blockingGet();
        assertTrue(list.size() > 0);
        assertTrue(list.contains(folderNameOfResource));
        for (var folderName : list) {
            assertTrue(StringUtils.isNotBlank(folderName));
            assertTrue(!folderName.contains("/"));
            assertTrue(!folderName.contains("\\"));
            assertTrue(this.storage.isDirectory(folderName));
        }
    }

    @BeforeEach
    public void beforeEach() {
        var storageFileModel = this.storage
                .storageResource(new ClassPathResource("image/default.jpg"));
        this.folderNameOfResource = storageFileModel.getFolderName();
        this.request.setRequestURI(storageFileModel.getRelativeUrl());
    }
}
