package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.springboot.project.model.OrganizeMoveTopModel;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeMoveTopService;
import com.springboot.project.service.OrganizeService;

@Component
public class OrganizeUtil {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private OrganizeClosureService organizeClosureService;

    @Autowired
    private OrganizeMoveTopService organizeMoveTopService;

    private Long pageSize = 1L;

    public void move(String id, String parentId) throws InterruptedException {
        OrganizeMoveTopModel[] organizeMoveTopList;
        var initStartDate = new Date();
        while (true) {
            try {
                organizeMoveTopList = this.organizeMoveTopService.createOrganizeMoveTop(id, parentId);
                break;
            } catch (DataIntegrityViolationException e) {
                if (!initStartDate.before(DateUtils.addSeconds(new Date(), -5))) {
                    Thread.sleep(1);
                    continue;
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Too many requests to move the organize, please wait a minute and try again");
            }
        }
        try {
            this.organizeService.checkOrganizeCanBeMove(id, parentId);
            this.organizeService.move(id, parentId);
        } finally {
            this.organizeMoveTopService.deleteOrganizeMoveTop(organizeMoveTopList);
        }

        this.refresh(id);
    }

    public void refresh(String organizeId) {
        var deadline = this.getDeadline();
        var maxDeep = 1000L;
        this.refresh(organizeId, deadline, maxDeep);
    }

    public void refresh(String organizeId, Date deadline, Long maxDeep) {
        if (!deadline.before(new Date())) {
            return;
        }
        if (maxDeep <= 0) {
            return;
        }

        while (true) {
            if (!deadline.before(new Date())) {
                return;
            }
            var hasNext = this.organizeClosureService.refresh(organizeId);
            if (!hasNext) {
                break;
            }
        }

        while (true) {
            if (!deadline.before(new Date())) {
                return;
            }
            var totalPage = this.organizeService.getChildOrganizeListThatContainsDeleted(1L, pageSize, organizeId)
                    .getTotalPage();
            for (var pageNum = totalPage; pageNum > 0; pageNum--) {
                var list = this.organizeService.getChildOrganizeListThatContainsDeleted(1L, pageSize, organizeId)
                        .getList();
                for (var organize : list) {
                    this.refresh(organize.getId(), deadline, maxDeep - 1);
                }
            }
        }
    }

    private Date getDeadline() {
        return DateUtils.addSeconds(new Date(), 10);
    }

}
