package com.springboot.project.test.service.StorageSpaceService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.uuid.Generators;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.Test;
import com.springboot.project.test.BaseTest;
import io.reactivex.rxjava3.core.Flowable;

public class StorageSpaceServiceDeleteStorageSpaceEntityTest extends BaseTest {
    private String folderName = Generators.timeBasedGenerator().generate().toString();

    @Test
    public void test() {
        this.storageSpaceService.deleteStorageSpaceEntity(folderName);
        var totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1L, 1L).getTotalPage();
        var list = Flowable.range(1, totalPage.intValue()).concatMap((s) -> {
            Long pageNum = Integer.valueOf(s).longValue();
            return Flowable
                    .fromIterable(this.storageSpaceService.getStorageSpaceListByPagination(pageNum, 1L).getList());
        }).toList().blockingGet();
        assertTrue(JinqStream.from(list).where(s -> s.getFolderName().equals(folderName)).exists());
    }
}
