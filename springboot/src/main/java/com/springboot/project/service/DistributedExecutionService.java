package com.springboot.project.service;

import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.enumerate.DistributedExecutionEnum;
import com.springboot.project.model.DistributedExecutionModel;

@Service
public class DistributedExecutionService extends BaseService {

    @Autowired
    private StorageSpaceService storageSpaceService;

    @Autowired
    private OrganizeService organizeService;

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
            var totalPage = this.storageSpaceService.getStorageSpaceListByPagination(1L, pageSize).getTotalPage();
            if (totalPage > 0) {
                pageNum = totalPage;
            }
        }

        return this.createDistributedExecution(DistributedExecutionEnum.STORAGE_SPACE_CLEAN_DATABASE_STORAGE, version,
                pageNum, pageSize);
    }

    public DistributedExecutionModel getDistributedExecutionOfOrganize(Long pageSize) {
        var name = DistributedExecutionEnum.ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY.name();
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
            var totalPage = this.organizeService.getOrganizeListThatContainsDeleted(1L, pageSize).getTotalPage();
            if (totalPage > 0) {
                pageNum = totalPage;
            }
        }

        return this.createDistributedExecution(DistributedExecutionEnum.ORGANIZE_REFRESH_ORGANIZE_CLOSURE_ENTITY,
                version,
                pageNum, pageSize);
    }

    private DistributedExecutionModel createDistributedExecution(DistributedExecutionEnum distributedExecutionEnum,
            String version, Long pageNum, Long pageSize) {
        var distributedExecutionEntity = new DistributedExecutionEntity();
        distributedExecutionEntity.setId(newId());
        distributedExecutionEntity.setName(distributedExecutionEnum.name());
        if (StringUtils.isBlank(version)) {
            distributedExecutionEntity.setVersion(
                    FastDateFormat.getInstance(dateFormatProperties.getYearMonthDayHourMinuteSecond(), TimeZone.getTimeZone("UTC")).format(new Date())
                            + " "
                            + Generators.timeBasedReorderedGenerator().generate().toString());
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
