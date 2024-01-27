package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel()
                .setId(organizeEntity.getId())
                .setName(organizeEntity.getName())
                .setCreateDate(organizeEntity.getCreateDate())
                .setUpdateDate(organizeEntity.getUpdateDate())
                .setChildCount(0L)
                .setDescendantCount(0L)
                .setChildList(Lists.newArrayList());

        var id = organizeEntity.getId();

        var level = this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .select(s -> JPQLFunction.getAncestorCountOfOrganize(s.getId()))
                .getOnlyValue();
        organizeModel.setLevel(level);

        if (organizeEntity.getParent() != null) {
            organizeModel.setParent(new OrganizeModel().setId(organizeEntity.getParent().getId()));
        }

        var isDeleted = !this.OrganizeEntity()
                .where(s -> s.getId().equals(id))
                .select(s -> JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .getOnlyValue();
        organizeModel.setIsDeleted(isDeleted);
        if (!isDeleted) {
            var childOrganizeCount = this.OrganizeEntity()
                    .where(s -> s.getParent().getId().equals(id))
                    .where(s -> !s.getIsDeleted())
                    .count();
            organizeModel.setChildCount(childOrganizeCount);

            var descendantCount = this.OrganizeEntity()
                    .where(s -> s.getId().equals(id))
                    .select(s -> JPQLFunction.getDescendantCountOfOrganize(s.getId()))
                    .getOnlyValue();
            organizeModel.setDescendantCount(descendantCount);
        }
        return organizeModel;
    }

}
