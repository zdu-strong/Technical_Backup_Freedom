package com.springboot.project.format;

import java.util.ArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.google.common.collect.Lists;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.OrganizeEntity;
import com.springboot.project.model.OrganizeModel;

@Service
public class OrganizeFormatter extends BaseService {

    public OrganizeModel format(OrganizeEntity organizeEntity) {
        var organizeModel = new OrganizeModel();
        BeanUtils.copyProperties(organizeEntity, organizeModel);
        organizeModel.setChildCount(0L)
                .setDescendantCount(0L)
                .setChildList(Lists.newArrayList());

        var id = organizeEntity.getId();

        organizeModel.setLevel(this.getLevel(organizeEntity));

        if (organizeEntity.getParent() != null) {
            organizeModel.setParent(new OrganizeModel().setId(organizeEntity.getParent().getId()));
        }

        organizeModel.setIsDeleted(this.isDeleted(organizeEntity));
        if (!organizeModel.getIsDeleted()) {
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
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeEntity == null) {
                break;
            }
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            if (organizeEntity.getIsDeleted()) {
                isDeleted = true;
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return isDeleted;
    }

    private long getLevel(OrganizeEntity organizeEntity) {
        var level = 0L;
        var parent = organizeEntity.getParent();
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (parent == null) {
                break;
            }
            if (organizeIdList.contains(parent.getId())) {
                break;
            }
            level++;
            organizeIdList.add(parent.getId());
            parent = parent.getParent();
        }
        return level;
    }

}
