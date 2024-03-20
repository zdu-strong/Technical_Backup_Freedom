package com.springboot.project.format;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeMoveTopEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.OrganizeMoveTopModel;

@Service
public class OrganizeMoveTopFormatter extends BaseService {

    public OrganizeMoveTopModel format(OrganizeMoveTopEntity organizeMoveTopEntity) {
        var organizeMoveTopModel = new OrganizeMoveTopModel();
        BeanUtils.copyProperties(organizeMoveTopEntity, organizeMoveTopModel);
        organizeMoveTopModel.setOrganize(new OrganizeModel().setId(organizeMoveTopEntity.getOrganize().getId()));
        return organizeMoveTopModel;
    }

}
