package com.springboot.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jinq.orm.stream.JinqStream;
import org.springframework.stereotype.Service;
import com.springboot.project.common.baseService.BaseService;
import com.springboot.project.entity.*;
import com.springboot.project.model.OrganizeMoveTopModel;

@Service
public class OrganizeMoveTopService extends BaseService {

    public OrganizeMoveTopModel[] createOrganizeMoveTop(String... organizeId) {
        var topOrganizeList = JinqStream.from(List.of(organizeId))
                .where(s -> StringUtils.isNotBlank(s))
                .select(s -> getTopOrganize(s))
                .group(s -> s.getId(), (s, t) -> t.findFirst().get())
                .select(s -> s.getTwo())
                .toList();
        for (var topOrganize : topOrganizeList) {
            var topOrganizeId = topOrganize.getId();
            var expireDate = DateUtils.addMinutes(new Date(), -1);
            var organizeMoveTopEntity = this.OrganizeMoveTopEntity()
                    .where(s -> s.getOrganize().getId().equals(topOrganizeId))
                    .where(s -> s.getCreateDate().before(expireDate))
                    .findFirst()
                    .orElse(null);
            if (organizeMoveTopEntity == null) {
                continue;
            }
            this.remove(organizeMoveTopEntity);
        }
        var organizeMoveTopList = new ArrayList<OrganizeMoveTopModel>();
        for (var topOrganize : topOrganizeList) {
            var organizeMoveTopEntity = new OrganizeMoveTopEntity();
            organizeMoveTopEntity.setId(newId());
            organizeMoveTopEntity.setCreateDate(new Date());
            organizeMoveTopEntity.setUpdateDate(new Date());
            organizeMoveTopEntity.setOrganize(topOrganize);
            this.persist(organizeMoveTopEntity);

            organizeMoveTopList.add(this.organizeMoveTopFormatter.format(organizeMoveTopEntity));
        }
        return organizeMoveTopList.toArray(new OrganizeMoveTopModel[] {});
    }

    private OrganizeEntity getTopOrganize(String organizeId) {
        var organizeEntity = this.OrganizeEntity().where(s -> s.getId().equals(organizeId)).getOnlyValue();
        var organizeIdList = new ArrayList<String>();
        while (true) {
            if (organizeIdList.contains(organizeEntity.getId())) {
                break;
            }
            if (organizeEntity.getParent() == null) {
                break;
            }
            organizeIdList.add(organizeEntity.getId());
            organizeEntity = organizeEntity.getParent();
        }
        return organizeEntity;
    }

    public void deleteOrganizeMoveTop(OrganizeMoveTopModel... organizeMoveTopModel) {
        for (var organizeMoveTop : organizeMoveTopModel) {
            var id = organizeMoveTop.getId();
            var organizeMoveTopEntity = this.OrganizeMoveTopEntity()
                    .where(s -> s.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            if (organizeMoveTopEntity == null) {
                continue;
            }
            this.remove(organizeMoveTopEntity);
        }
    }

}
