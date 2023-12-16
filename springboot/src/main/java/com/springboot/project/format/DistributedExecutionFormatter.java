package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.springboot.project.entity.DistributedExecutionEntity;
import com.springboot.project.model.DistributedExecutionModel;
import com.springboot.project.service.BaseService;

@Service
public class DistributedExecutionFormatter extends BaseService {

    public DistributedExecutionModel format(DistributedExecutionEntity distributedExecutionEntity) {
        var distributedExecutionModel = new DistributedExecutionModel()
                .setId(distributedExecutionEntity.getId())
                .setName(distributedExecutionEntity.getName())
                .setVersion(distributedExecutionEntity.getVersion())
                .setPageSize(distributedExecutionEntity.getPageSize())
                .setPageNum(distributedExecutionEntity.getPageNum())
                .setCreateDate(distributedExecutionEntity.getCreateDate())
                .setUpdateDate(distributedExecutionEntity.getUpdateDate());
        return distributedExecutionModel;
    }

}
