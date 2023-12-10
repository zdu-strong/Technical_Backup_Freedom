package com.springboot.project.scheduled;

import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.IsTestOrDevModeProperties;
import com.springboot.project.service.StorageSpaceService;
import io.reactivex.rxjava3.core.Observable;

@Component
public class StorageSpaceScheduled {

    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private Storage storage;

    @Autowired
    private IsTestOrDevModeProperties isTestOrDevModeProperties;

    private Long pageSize = 1L;

    @Scheduled(initialDelay = 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() {
        if (this.isTestOrDevModeProperties.getIsTestOrDevMode()) {
            return;
        }

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(() -> {
                this.cleanDatabaseStorage();
            });
            executor.submit(() -> {
                this.cleanDiskStorage();
            });
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
        long totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1L, pageSize).getTotalPage();
        Observable.rangeLong(1, totalPage).concatMap((s) -> {
            var pageNum = totalPage - s + 1;
            return Observable.just(pageNum);
        }).concatMap(pageNum -> {
            return Observable.just("").concatMap(s -> {
                var list = this.storageSpaceService
                        .getStorageSpaceListByPagination(pageNum, pageSize)
                        .getList();
                return Observable.fromIterable(list);
            }).retry(10);
        }).concatMap(storageSpaceModel -> {
            return Observable.just("").concatMap((s) -> {
                if (!this.storageSpaceService.isUsed(storageSpaceModel.getFolderName())) {
                    this.storageSpaceService.deleteStorageSpaceEntity(storageSpaceModel.getFolderName());
                }
                return Observable.empty();
            }).retry(10);
        }).blockingSubscribe();
    }
}
