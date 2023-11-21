package com.springboot.project.service;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeModel;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeEntity;

@Service
public class OrganizeService extends BaseService {

    public OrganizeModel create(OrganizeModel organizeModel) {
        var parentOrganize = this.getParentOrganize(organizeModel);
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(Generators.timeBasedGenerator().generate().toString());
        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setIsDeleted(false);
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        this.persist(organizeEntity);

        if (organizeModel.getChildList() != null) {
            for (var childOrganize : organizeModel.getChildList()) {
                childOrganize.setParent(new OrganizeModel().setId(organizeEntity.getId()));
                this.create(childOrganize);
            }
        }

        return this.organizeFormatter.format(organizeEntity);
    }

    public void update(OrganizeModel organizeModel) {
        var id = organizeModel.getId();
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(id)).getOnlyValue();

        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    public void delete(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(id)).getOnlyValue();
        organizeEntity.setParent(null);
        this.remove(organizeEntity);
    }

    public OrganizeModel getById(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(id)).getOnlyValue();

        return this.organizeFormatter.format(organizeEntity);
    }

    public void checkExistOrganize(String id) {
        var exists = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeleteOfOrganizeAndAncestors(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    private OrganizeEntity getParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParent() == null ? null : organizeModel.getParent().getId();
        if (StringUtils.isBlank(parentOrganizeId)) {
            return null;
        }

        var parentOrganize = this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId)).getOnlyValue();
        return parentOrganize;
    }

}
