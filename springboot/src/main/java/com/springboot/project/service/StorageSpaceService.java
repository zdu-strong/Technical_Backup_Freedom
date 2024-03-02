package com.springboot.project.service;

import java.nio.file.Paths;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.StorageSpaceEntity;
import com.springboot.project.enumerate.StorageSpaceEnum;
import com.springboot.project.model.PaginationModel;
import com.springboot.project.model.StorageSpaceModel;

@Service
public class StorageSpaceService extends BaseService {

    public PaginationModel<StorageSpaceModel> getStorageSpaceListByPagination(Long pageNum, Long pageSize) {
        var stream = this.StorageSpaceEntity().sortedBy(s -> s.getId()).sortedBy(s -> s.getCreateDate());
        var storageSpacePaginationModel = new PaginationModel<>(pageNum, pageSize, stream,
                (s) -> this.storageSpaceFormatter.format(s));
        return storageSpacePaginationModel;
    }

    @SuppressWarnings("resource")
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

        var expireDate = DateUtils.addMilliseconds(new Date(),
                Long.valueOf(0 - StorageSpaceEnum.TEMP_FILE_SURVIVAL_DURATION.toMillis()).intValue());
        var isUsed = !this.StorageSpaceEntity()
                .where(s -> s.getFolderName().equals(folderName))
                .where(s -> s.getUpdateDate().before(expireDate))
                .leftOuterJoin((s, t) -> t.stream(StorageSpaceEntity.class),
                        (s, t) -> s.getFolderName().equals(t.getFolderName())
                                && !t.getUpdateDate().before(expireDate))
                .where(s -> s.getTwo() == null)
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
