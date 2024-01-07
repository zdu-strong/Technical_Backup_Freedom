package com.springboot.project.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.enumeration.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionModel;

@Service
public class DistributedExecutionService extends BaseService {

    @Autowired
    private StorageSpaceService storageSpaceService;

    public DistributedExecutionModel getDistributedExecutionOfStorageSpace(Long pageSize) {
        var name = DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE.name();
        var distributedExecutionEntityOptional = this.DistributedExecutionEntity()
                .where(s -> s.getName().equals(name))
                .where(s -> s.getPageSize() == pageSize)
                .sortedBy(s -> s.getPageNum())
                .sortedDescendingBy(s -> s.getVersion())
                .findFirst();
        var pageNum = distributedExecutionEntityOptional.map(s -> s.getPageNum()).orElse(1L);
        var version = pageNum > 1 ? distributedExecutionEntityOptional.map(s -> s.getVersion()).get() : null;
        if (pageNum > 1) {
            pageNum--;
        } else {
            var totalPage = storageSpaceService.getStorageSpaceListByPagination(1L, pageSize).getTotalPage();
            if (totalPage > 0) {
                pageNum = totalPage;
            }
        }

        return this.createDistributedExecution(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE, version,
                pageNum, pageSize);
    }

    private DistributedExecutionModel createDistributedExecution(DistributedExecutionEnum distributedExecutionEnum,
            String version, Long pageNum, Long pageSize) {
        var distributedExecutionEntity = new DistributedExecutionEntity();
        distributedExecutionEntity.setId(newId());
        distributedExecutionEntity.setName(distributedExecutionEnum.name());
        if (StringUtils.isBlank(version)) {
            var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(this.timeZoneUtil.getTimeZoneFromUTC()));
            distributedExecutionEntity.setVersion(
                    simpleDateFormat.format(new Date()) + " " + Generators.timeBasedGenerator().generate().toString());
        } else {
            distributedExecutionEntity.setVersion(version);
        }
        distributedExecutionEntity.setPageNum(pageNum);
        distributedExecutionEntity.setPageSize(pageSize);
        distributedExecutionEntity.setCreateDate(new Date());
        distributedExecutionEntity.setUpdateDate(new Date());
        this.persist(distributedExecutionEntity);

        return this.distributedExecutionFormatter.format(distributedExecutionEntity);
    }

}
