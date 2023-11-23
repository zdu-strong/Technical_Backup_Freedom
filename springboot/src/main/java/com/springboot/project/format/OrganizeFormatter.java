package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;
import com.springboot.project.service.BaseService;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel()
                .setId(organizeEntity.getId())
                .setName(organizeEntity.getName())
                .setCreateDate(organizeEntity.getCreateDate())
                .setUpdateDate(organizeEntity.getUpdateDate())
                .setChildList(Lists.newArrayList());

        var id = organizeEntity.getId();

        var level = this.OrganizeEntity()
                .where(s -> JPQLFunction.isChildOfOrganize(id, s.getId()))
                .where(s -> !s.getId().equals(id))
                .count();
        organizeModel.setLevel(level);

        if (organizeEntity.getParent() != null) {
            organizeModel.setParent(new OrganizeModel().setId(organizeEntity.getParent().getId()));
        }

        var childOrganizeCount = this.OrganizeEntity()
                .where(s -> s.getParent().getId().equals(id))
                .where(s -> !s.getIsDeleted())
                .count();
        organizeModel.setChildCount(childOrganizeCount);

        var descendantCount = this.OrganizeEntity()
                .where(s -> JPQLFunction.isChildOfOrganize(s.getId(), id))
                .where(s -> JPQLFunction.isNotDeletedOfOrganize(s.getId()))
                .where(s -> !s.getId().equals(id))
                .count();
        organizeModel.setDescendantCount(descendantCount);
        return organizeModel;
    }

}
