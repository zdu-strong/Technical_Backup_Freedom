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
        organizeEntity
                .setDeleteKey(parentOrganize == null ? Generators.timeBasedGenerator().generate().toString() : "");
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        this.persist(organizeEntity);

        return this.organizeFormatter.format(organizeEntity);
    }

    public void update(OrganizeModel organizeModel) {
        var id = organizeModel.getId();
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).getOnlyValue();

        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    public void delete(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).getOnlyValue();
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setIsDeleted(true);
        organizeEntity.setDeleteKey(Generators.timeBasedGenerator().generate().toString());
        this.merge(organizeEntity);
    }

    public OrganizeModel getById(String id) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).getOnlyValue();

        return this.organizeFormatter.format(organizeEntity);
    }

    public void checkExistOrganize(String id) {
        var exists = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    public void move(String id, String parentId) {
        var parentOrganizeEntity = this
                .getParentOrganize(new OrganizeModel().setParent(new OrganizeModel().setId(parentId)));
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).getOnlyValue();
        organizeEntity.setParent(parentOrganizeEntity);
        organizeEntity.setDeleteKey(
                parentOrganizeEntity == null ? Generators.timeBasedGenerator().generate().toString() : "");
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    private OrganizeEntity getParentOrganize(OrganizeModel organizeModel) {
        var parentOrganizeId = organizeModel.getParent() == null ? null : organizeModel.getParent().getId();
        if (StringUtils.isBlank(parentOrganizeId)) {
            return null;
        }

        var parentOrganizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(parentOrganizeId))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(s.getId())).getOnlyValue();
        return parentOrganizeEntity;
    }

}
