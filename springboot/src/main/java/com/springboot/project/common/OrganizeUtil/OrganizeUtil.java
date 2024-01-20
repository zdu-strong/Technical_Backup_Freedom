package com.springboot.project.common.OrganizeUtil;

import java.util.Calendar;
import java.util.Date;
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

    public Date getDeadline() {
        var calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);
        return calendar.getTime();
    }

    public void refresh(String organizeId, Date deadline) {
        if (!deadline.before(new Date())) {
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
                    this.refresh(organize.getId(), deadline);
                }
            }
        }
    }

}
