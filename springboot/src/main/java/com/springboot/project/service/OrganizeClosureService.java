package com.springboot.project.service;

import java.util.Date;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.common.database.JPQLFunction;
import com.springboot.project.entity.OrganizeClosureEntity;

@Service
public class OrganizeClosureService extends BaseService {

    public boolean refresh(String organizeId) {
        {
            var isDeleted = !this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                    .where(s -> JPQLFunction.isNotDeletedOfOrganize(organizeId)).exists();
            if (isDeleted) {
                var ancestorList = this.OrganizeClosureEntity().where(s -> s.getDescendant().getId().equals(organizeId))
                        .toList();
                for (var ancestor : ancestorList) {
                    this.remove(ancestor);
                }
                return false;
            }
        }

        {
            var level = this.OrganizeEntity()
                    .where(s -> s.getId().equals(organizeId))
                    .select(s -> JPQLFunction.getAncestorCountOfOrganize(s.getId()))
                    .getOnlyValue();

            var ancestorCount = this.OrganizeClosureEntity().where(s -> s.getDescendant().getId().equals(organizeId))
                    .count();
            if (ancestorCount <= level) {
                var ancestorOrganizetity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId))
                        .getOnlyValue();
                while (true) {
                    var ancestorOrganizeId = ancestorOrganizetity.getId();
                    var descendantOrganizeId = organizeId;
                    var exists = this.OrganizeClosureEntity()
                            .where(s -> s.getAncestor().getId().equals(ancestorOrganizeId))
                            .where(s -> s.getDescendant().getId().equals(descendantOrganizeId))
                            .exists();
                    if (!exists) {
                        this.create(ancestorOrganizeId, descendantOrganizeId);
                        return true;
                    }
                    ancestorOrganizetity = ancestorOrganizetity.getParent();
                    if (ancestorOrganizetity == null) {
                        break;
                    }
                }
            }
        }

        {
            var organizeClosureEntityOptional = this.OrganizeClosureEntity()
                    .where(s -> s.getDescendant().getId().equals(organizeId))
                    .where(s -> !JPQLFunction.isChildOfOrganize(s.getDescendant().getId(), s.getAncestor().getId()))
                    .findFirst();
            if (organizeClosureEntityOptional.isPresent()) {
                this.remove(organizeClosureEntityOptional.get());
                return true;
            }
        }

        return false;
    }

    private void create(String ancestorOrganizeId, String descendantOrganizeId) {
        var ancestor = this.OrganizeEntity().where(s -> s.getId().equals(ancestorOrganizeId)).getOnlyValue();
        var descendant = this.OrganizeEntity().where(s -> s.getId().equals(descendantOrganizeId)).getOnlyValue();
        var organizeClosureEntity = new OrganizeClosureEntity();
        organizeClosureEntity.setId(newId());
        organizeClosureEntity.setIsDeleted(false);
        organizeClosureEntity.setCreateDate(new Date());
        organizeClosureEntity.setUpdateDate(new Date());
        organizeClosureEntity.setAncestor(ancestor);
        organizeClosureEntity.setDescendant(descendant);
        this.persist(organizeClosureEntity);
    }

}
