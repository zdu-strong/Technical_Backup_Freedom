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
        organizeClosureEntity.setTrait((organize.getParent() == null ? ","
                : organize.getParent().getOrganizeClosure().getTrait()) + organize.getId() + ",");
        organizeClosureEntity.setOrganize(organize);
        this.persist(organizeClosureEntity);
    }

    public void update(String organizeId) {
        var organize = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var isDeleted = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                .select(s -> !JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .getOnlyValue();
        var organizeClosureEntity = organize.getOrganizeClosure();
        organizeClosureEntity.setIsDeleted(isDeleted);
        organizeClosureEntity.setTrait((organize.getParent() == null ? ","
                : organize.getParent().getOrganizeClosure().getTrait()) + organize.getId() + ",");
        organizeClosureEntity.setUpdateDate(new Date());
        this.merge(organizeClosureEntity);
    }

    /**
     * 
     * @return boolean hasNext
     */
    public boolean refresh() {
        OrganizeClosureEntity organizeClosureEntity = this.OrganizeClosureEntity()
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(s.getOrganize().getId()))
                .where(s -> s.getIsDeleted())
                .findFirst()
                .orElse(null);

        if (organizeClosureEntity == null) {
            organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> !JPQLFunction.isNotDeletedOfOrganize(s.getOrganize().getId()))
                    .where(s -> !s.getIsDeleted())
                    .findFirst()
                    .orElse(null);
        }

        if (organizeClosureEntity != null) {
            this.update(organizeClosureEntity.getOrganize().getId());
        }

        {
            var hasNext = organizeClosureEntity != null;
            if (hasNext) {
                return hasNext;
            }
        }

        organizeClosureEntity = this.OrganizeClosureEntity()
                .where(s -> s.getOrganize().getParent() == null)
                .where(s -> s.getTrait() != "," + s.getOrganize().getId() + ",")
                .findFirst().orElse(null);

        if (organizeClosureEntity == null) {
            organizeClosureEntity = this.OrganizeClosureEntity()
                    .where(s -> s.getOrganize().getParent() != null)
                    .where(s -> !s.getTrait().equals(","
                            + s.getOrganize().getParent().getOrganizeClosure().getTrait()
                            + s.getOrganize().getId()))
                    .findFirst().orElse(null);
        }

        if (organizeClosureEntity != null) {
            this.update(organizeClosureEntity.getOrganize().getId());
        }

        {
            var hasNext = organizeClosureEntity != null;
            return hasNext;
        }

    }

}
