package com.springboot.project.common.OrganizeUtil;

import java.util.Date;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeService;

@Component
public class OrganizeUtil {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private OrganizeClosureService organizeClosureService;

    private Long pageSize = 1L;

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

        this.organizeClosureService.update(organizeId);

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
