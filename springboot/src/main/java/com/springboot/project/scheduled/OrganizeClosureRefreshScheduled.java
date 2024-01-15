package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.service.OrganizeClosureService;

@Component
public class OrganizeClosureRefreshScheduled {

    @Autowired
    private OrganizeClosureService organizeClosureService;

    @Scheduled(initialDelay = 1000 * 60, fixedDelay = 1000)
    public void scheduled() {
        this.refresh();
    }

    public void refresh() {
        var hasNext = true;
        while (hasNext) {
            hasNext = this.organizeClosureService.refresh();
        }
    }
}
