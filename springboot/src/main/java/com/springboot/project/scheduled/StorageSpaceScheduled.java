package com.springboot.project.scheduled;

import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.properties.IsTestOrDevModeProperties;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.StorageSpaceService;
import io.reactivex.rxjava3.core.Flowable;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    @Autowired
    private IsTestOrDevModeProperties isTestOrDevModeProperties;

    private Long pageSize = 1L;

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() throws InterruptedException, ExecutionException {
        if (this.isTestOrDevModeProperties.getIsTestOrDevMode()) {
            return;
        }

        this.cleanDatabaseStorage();
    }

    public void cleanDatabaseStorage() {
        Long pageNumOfGlobal = null;
        while (true) {
            var pageNumOfThis = Flowable.just("")
                    .concatMap(s -> {
                        var pageNum = this.distributedExecutionService.getDistributedExecutionOfStorageSpace(pageSize)
                                .getPageNum();
                        var list = this.storageSpaceService
                                .getStorageSpaceListByPagination(pageNum, pageSize)
                                .getList();
                        for (var storageSpaceModel : list) {
                            this.storageSpaceService.refresh(storageSpaceModel.getFolderName());
                        }
                        return Flowable.just(pageNum);
                    })
                    .retry(1000)
                    .blockingLast();
            if (pageNumOfGlobal == null || pageNumOfThis < pageNumOfGlobal) {
                pageNumOfGlobal = pageNumOfThis;
            }
            if (pageNumOfThis > pageNumOfGlobal) {
                break;
            }
            if (pageNumOfThis == 1) {
                break;
            }
        }
    }
}
