package com.springboot.project.scheduled;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.properties.StorageRootPathProperties;
import com.springboot.project.service.OrganizeService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FixConcurrencyMoveOrganizeScheduled {

    @Autowired
    private OrganizeService organizeService;

    @Autowired
    private StorageRootPathProperties storageRootPathProperties;

    @Scheduled(initialDelay = 1000, fixedDelay = 1000)
    public void scheduled() {
        if (this.storageRootPathProperties.isTestEnviroment()) {
            return;
        }

        try {
            this.organizeService.fixConcurrencyMoveOrganize();
        } catch (Throwable e) {
            log.error("Failed to fix concurrency move organize", e);
        }
    }
}
