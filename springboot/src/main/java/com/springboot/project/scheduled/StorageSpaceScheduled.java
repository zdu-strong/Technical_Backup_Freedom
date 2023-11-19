package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.StorageSpaceService;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StorageSpaceScheduled {
    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private Storage storage;

    @Autowired
    private StorageRootPathProperties storageRootPathProperties;

    private Long pageSize = 1L;

    @Scheduled(initialDelay = 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() {
        if (this.storageRootPathProperties.isTestEnviroment()) {
            return;
        }

        {
            try {
                long totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1L, pageSize).getTotalPage();
                for (var pageNum = totalPage; pageNum > 0; pageNum--) {
                    try {
                        for (var storageSpaceModel : this.storageSpaceService
                                .getStorageSpaceListByPagination(pageNum, pageSize)
                                .getList()) {
                            if (!this.storageSpaceService.isUsed(storageSpaceModel.getFolderName())) {
                                this.storageSpaceService.deleteStorageSpaceEntity(storageSpaceModel.getFolderName());
                            }
                        }
                    } catch (Throwable e) {
                        log.error("Failed to delete folder of database records", e);
                    }
                }
            } catch (Throwable e) {
                log.error("Failed to get number of folders for database records", e);
            }
        }

        {
            try {
                this.storage.listRoots().concatMap(folderName -> {
                    try {
                        if (!this.storageSpaceService.isUsed(folderName)) {
                            this.storageSpaceService.deleteStorageSpaceEntity(folderName);
                        }
                    } catch (Throwable e) {
                        log.error("Failed to delete local folder \"" + folderName + "\"", e);
                    }
                    return Observable.empty();
                }).blockingSubscribe();
            } catch (Throwable e) {
                log.error("Failed to get list of files in root directory", e);
            }
        }
    }
}
