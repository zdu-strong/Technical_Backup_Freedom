package com.springboot.project.format;

import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
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

        var level = this.getLevel(organizeEntity);
        organizeModel.setLevel(level);

        if (organizeEntity.getParent() != null) {
            organizeModel.setParent(new OrganizeModel().setId(organizeEntity.getParent().getId()));
        }

        var isDeleted = this.isDeleted(organizeEntity);
        organizeModel.setIsDeleted(isDeleted);
        if (!isDeleted) {
            var childOrganizeCount = this.OrganizeEntity()
                    .where(s -> s.getParent().getId().equals(id))
                    .where(s -> !s.getIsDeleted())
                    .count();
            organizeModel.setChildCount(childOrganizeCount);

            var descendantCount = this.OrganizeClosureEntity().where(s -> s.getAncestor().getId().equals(id))
                    .where(s -> !s.getIsDeleted())
                    .where(s -> !s.getDescendant().getId().equals(id))
                    .count();
            organizeModel.setDescendantCount(descendantCount);
        }
        return organizeModel;
    }

    private boolean isDeleted(OrganizeEntity organizeEntity) {
        var isDeleted = false;
        while (true) {
            if (organizeEntity == null) {
                break;
            }
            if (organizeEntity.getIsDeleted()) {
                isDeleted = true;
                break;
            }
            organizeEntity = organizeEntity.getParent();
        }
        return isDeleted;
    }

    private long getLevel(OrganizeEntity organizeEntity) {
        var level = 0L;
        var parent = organizeEntity.getParent();
        while (true) {
            if (parent == null) {
                break;
            }
            level++;
            parent = parent.getParent();
        }
        return level;
    }

}
