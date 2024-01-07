package com.springboot.project.service;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.StorageSpaceModel;

@Service
public class StorageSpaceService extends BaseService {

    private Duration tempFileSurvivalDuration = Duration.ofDays(1);

    public PaginationModel<StorageSpaceModel> getStorageSpaceListByPagination(Long pageNum, Long pageSize) {
        var stream = this.StorageSpaceEntity().sortedBy(s -> s.getId()).sortedBy(s -> s.getCreateDate());
        var storageSpacePaginationModel = new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.storageSpaceFormatter.format(s));
        return storageSpacePaginationModel;
    }

    public boolean isUsed(String folderName) {
        this.checkIsValidFolderName(folderName);

        if (this.isUsedByProgramData(folderName)) {
            var list = this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).toList();
            for (var storageSpaceEntity : list) {
                this.remove(storageSpaceEntity);
            }
            return true;
        }

        if (!this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).exists()) {
            this.createStorageSpaceEntity(folderName);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MILLISECOND, Long.valueOf(0 - this.tempFileSurvivalDuration.toMillis()).intValue());
        Date expireDate = calendar.getTime();
        var isUsed = !this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
                .where((s, t) -> !t.stream(StorageSpaceEntity.class).where(m -> m.getFolderName().equals(folderName))
                        .where(m -> expireDate.before(m.getUpdateDate())).exists())
                .exists();
        return isUsed;
    }

    public void deleteStorageSpaceEntity(String folderName) {
        this.checkIsValidFolderName(folderName);
        if (this.isUsed(folderName)) {
            return;
        }
        for (var storageSpaceEntity : this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName))
                .toList()) {
            this.remove(storageSpaceEntity);
        }
        this.storage.delete(folderName);
    }

    private StorageSpaceModel createStorageSpaceEntity(String folderName) {
        this.checkIsValidFolderName(folderName);
        if (this.StorageSpaceEntity().where(s -> s.getFolderName().equals(folderName)).exists()) {
            StorageSpaceEntity storageSpaceEntity = this.StorageSpaceEntity()
                    .where(s -> s.getFolderName().equals(folderName)).findFirst().get();
            storageSpaceEntity.setUpdateDate(new Date());
            this.merge(storageSpaceEntity);

            return this.storageSpaceFormatter.format(storageSpaceEntity);
        } else {
            StorageSpaceEntity storageSpaceEntity = new StorageSpaceEntity();
            storageSpaceEntity.setId(newId());
            storageSpaceEntity.setFolderName(folderName);
            storageSpaceEntity.setCreateDate(new Date());
            storageSpaceEntity.setUpdateDate(new Date());
            this.persist(storageSpaceEntity);

            return this.storageSpaceFormatter.format(storageSpaceEntity);
        }
    }

    private boolean isUsedByProgramData(String folderName) {
        if (this.UserMessageEntity().where(s -> s.getFolderName().equals(folderName)).exists()) {
            return true;
        }
        return false;
    }

    private void checkIsValidFolderName(String folderName) {
        if (StringUtils.isBlank(folderName)) {
            throw new RuntimeException("Folder name cannot be empty");
        }
        if (folderName.contains("/") || folderName.contains("\\")) {
            throw new RuntimeException("Folder name is invalid");
        }
        if (Paths.get(folderName).isAbsolute()) {
            throw new RuntimeException("Folder name is invalid");
        }
    }

}
