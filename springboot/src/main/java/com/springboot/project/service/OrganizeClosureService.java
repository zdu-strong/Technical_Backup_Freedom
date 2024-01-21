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
        var trait = this.getTrait(organizeId);
        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(newId());
        organizeClosureEntity.setIsDeleted(isDeleted);
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setTrait(trait);
        organizeClosureEntity.setOrganize(organize);
        this.persist(organizeClosureEntity);
    }

    public void update(String organizeId) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var isDeleted = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .select(s -> !JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .getOnlyValue();
        var trait = this.getTrait(organizeId);
        var organizeClosureEntity = organize.getOrganizeClosure();
        if (organizeClosureEntity.getIsDeleted() == isDeleted && organizeClosureEntity.getTrait().equals(trait)) {
            return;
        }

        organizeClosureEntity.setIsDeleted(isDeleted);
        organizeClosureEntity.setTrait(trait);
        organizeClosureEntity.setUpdateDate(new Date());
        this.merge(organizeClosureEntity);
    }

    private String getTrait(String organizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var trait = "," + organizeEntity.getId() + ",";
        while (true) {
            var parentOrganizeEntity = organizeEntity.getParent();
            if (parentOrganizeEntity == null) {
                break;
            }
            trait = "," + parentOrganizeEntity.getId() + trait;
            organizeEntity = parentOrganizeEntity;
        }
        return trait;
    }

}
