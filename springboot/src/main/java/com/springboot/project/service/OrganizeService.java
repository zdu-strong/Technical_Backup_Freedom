package com.springboot.project.service;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.model.PaginationModel;
import com.fasterxml.uuid.Generators;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeEntity;

@Service
public class OrganizeService extends BaseService {

    @Autowired
    private OrganizeClosureService organizeClosureService;

    public OrganizeModel create(OrganizeModel organizeModel) {
        var parentOrganize = this.getParentOrganize(organizeModel);
        var organizeEntity = new OrganizeEntity();
        organizeEntity.setId(newId());
        organizeEntity.setName(organizeModel.getName());
        organizeEntity.setIsDeleted(false);
        organizeEntity
                .setDeletedKey("");
        organizeEntity.setCreateDate(new Date());
        organizeEntity.setUpdateDate(new Date());
        organizeEntity.setParent(parentOrganize);
        this.persist(organizeEntity);

        this.organizeClosureService.create(organizeEntity.getId());

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
        organizeEntity.setDeletedKey(Generators.timeBasedGenerator().generate().toString());
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

    public void checkExistOrganizeAllowEmpty(String id) {
        if (StringUtils.isBlank(id)) {
            return;
        }
        var exists = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).exists();
        if (!exists) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Organize does not exist");
        }
    }

    public PaginationModel<OrganizeModel> searchByName(Long pageNum, Long pageSize, String name, String organizeId) {
        var trait = this.OrganizeEntity()
                .where(s -> s.getId().equals(organizeId))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .select(s -> s.getOrganizeClosure().getTrait())
                .getOnlyValue();
        var stream = this.OrganizeClosureEntity()
                .where(s -> s.getTrait().contains(trait))
                .where(s -> !s.getIsDeleted())
                .where(s -> s.getOrganize().getName().contains(name))
                .select(s -> s.getOrganize());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
    }

    public void move(String id, String parentId) {
        var parentOrganizeEntity = this
                .getParentOrganize(new OrganizeModel().setParent(new OrganizeModel().setId(parentId)));
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(id)).getOnlyValue();
        organizeEntity.setParent(parentOrganizeEntity);
        organizeEntity.setUpdateDate(new Date());
        this.merge(organizeEntity);
    }

    public PaginationModel<OrganizeModel> getOrganizeListThatContainsDeleted(Long pageNum, Long pageSize) {
        var stream = this.OrganizeEntity().sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
    }

    public PaginationModel<OrganizeModel> getChildOrganizeListThatContainsDeleted(Long pageNum, Long pageSize,
            String organizeId) {
        var stream = this.OrganizeEntity().where(s -> s.getParent().getId().equals(organizeId))
                .sortedDescendingBy(s -> s.getId())
                .sortedDescendingBy(s -> s.getCreateDate());
        return new PaginationModel<>(pageNum, pageSize, stream, (s) -> this.organizeFormatter.format(s));
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
