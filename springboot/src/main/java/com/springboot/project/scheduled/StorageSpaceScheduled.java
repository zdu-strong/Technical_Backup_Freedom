package com.springboot.project.scheduled;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.IsTestOrDevModeProperties;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.StorageSpaceService;
import io.reactivex.rxjava3.core.Observable;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    @Autowired
    private Storage storage;

    @Autowired
    private IsTestOrDevModeProperties isTestOrDevModeProperties;

    private Long pageSize = 1L;

    @Scheduled(initialDelay = 60 * 60 * 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() throws InterruptedException, ExecutionException {
        if (this.isTestOrDevModeProperties.getIsTestOrDevMode()) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futureList = new ArrayList<Future<?>>();
            futureList.add(executor.submit(() -> {
                this.cleanDatabaseStorage();
            }));
            futureList.add(executor.submit(() -> {
                this.cleanDiskStorage();
            }));
            for (var future : futureList) {
                future.get();
            }
        }
    }

    public void cleanDiskStorage() {
        this.storage.listRoots().concatMap(folderName -> {
            return Observable.just("").concatMap((s) -> {
                if (!this.storageSpaceService.isUsed(folderName)) {
                    this.storageSpaceService.deleteStorageSpaceEntity(folderName);
                }
                return Observable.empty();
            }).retry(10);
        }).blockingSubscribe();
    }

    public void cleanDatabaseStorage() {
        Long pageNumOfGlobal = null;
        while (true) {
            var pageNumOfThis = Observable.just("")
                    .concatMap(s -> {
                        long pageNum = this.distributedExecutionService.getDistributedExecutionOfStorageSpace(pageSize)
                                .getPageNum();
                        var list = this.storageSpaceService
                                .getStorageSpaceListByPagination(pageNum, pageSize)
                                .getList();
                        for (var storageSpaceModel : list) {
                            if (!this.storageSpaceService.isUsed(storageSpaceModel.getFolderName())) {
                                this.storageSpaceService.deleteStorageSpaceEntity(storageSpaceModel.getFolderName());
                            }
                        }
                        return Observable.just(pageNum);
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
