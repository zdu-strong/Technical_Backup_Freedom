package com.springboot.project.scheduled;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.common.storage.Storage;
import com.springboot.project.service.StorageSpaceService;
import io.reactivex.rxjava3.core.Observable;

@Component
@EnableScheduling
public class StorageSpaceScheduled {
    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private Storage storage;

    private Logger log = LoggerFactory.getLogger(getClass());

    private int pageSize = 1;

    @Scheduled(initialDelay = 1000, fixedDelay = 60 * 60 * 1000)
    public void scheduled() {
        {
            try {
                int totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1, pageSize).getTotalPage();
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
        }
    }
}
