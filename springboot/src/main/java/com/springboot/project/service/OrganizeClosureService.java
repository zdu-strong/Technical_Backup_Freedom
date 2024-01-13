package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;

import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeClosureEntity;

@Service
public class OrganizeClosureService extends BaseService {

    public void create(String organizeId) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var isDeleted = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .select(s -> !JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .getOnlyValue();
        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(newId());
        organizeClosureEntity.setIsDeleted(isDeleted);
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setTrait(organize.getParent() == null ? ","
                : organize.getParent().getOrganizeClosure().getTrait() + organize.getId() + ",");
        organizeClosureEntity.setOrganize(organize);
        this.persist(organizeClosureEntity);
    }

}
