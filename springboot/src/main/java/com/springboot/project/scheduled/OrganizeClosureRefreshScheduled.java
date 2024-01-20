package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.service.DistributedExecutionService;
import com.springboot.project.service.OrganizeClosureService;
import com.springboot.project.service.OrganizeService;
import io.reactivex.rxjava3.core.Flowable;

@Component
public class OrganizeClosureRefreshScheduled {

    @Autowired
    private OrganizeClosureService organizeClosureService;

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private DistributedExecutionService distributedExecutionService;

    private Long pageSize = 1L;

    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000)
    public void scheduled() {
        this.refresh();
    }

    public void refresh() {
        Long pageNumOfGlobal = null;
        while (true) {
            var pageNumOfThis = Flowable.just("")
                    .concatMap(s -> {
                        var pageNum = this.distributedExecutionService.getDistributedExecutionOfOrganize(pageSize)
                                .getPageNum();
                        var list = this.organizeService
                                .getAllOrganize(pageNum, pageSize)
                                .getList();
                        for (var organizeModel : list) {
                            this.organizeClosureService.update(organizeModel.getId());
                        }
                        return Flowable.just(pageNum);
                    })
                    .retry(1000)
                    .blockingLast();
            if (pageNumOfGlobal == null || pageNumOfThis < pageNumOfGlobal) {
                pageNumOfGlobal = pageNumOfThis;
            }
            if (pageNumOfThis > pageNumOfGlobal) {
                break;
            }
            if (pageNumOfThis == 1) {
                break;
            }
        }
    }
}
