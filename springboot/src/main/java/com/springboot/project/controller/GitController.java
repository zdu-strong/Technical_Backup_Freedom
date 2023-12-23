package com.springboot.project.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.springboot.project.model.GitPropertiesModel;
import com.springboot.project.model.OrganizeModel;

@RestController
public class GitController extends BaseController {

    @GetMapping("/git")
    public ResponseEntity<?> getGitInfo() throws InterruptedException, ExecutionException {
        var pageSize = 1500;
        var list = getMany(pageSize);
        var startDate = new Date();
        var total = 150 * 1000 * 8;
        var totalPage = total / pageSize;
        this.run(totalPage, list);
        var endDate = new Date();
        var speed = totalPage * pageSize * 1000 / (endDate.getTime() - startDate.getTime());

        var gitPropertiesModel = new GitPropertiesModel().setCommitId(gitProperties.getCommitId())
                .setCommitDate(Date.from(gitProperties.getCommitTime()));
        return ResponseEntity.ok(gitPropertiesModel);
    }

    private void run(int totalPage, List<OrganizeModel> list) throws InterruptedException, ExecutionException {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futureList = new ArrayList<Future<?>>();
            var semaphore = new Semaphore(Runtime.getRuntime().availableProcessors());
            for (var i = totalPage; i > 0; i--) {
                futureList.add(executor.submit(() -> {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        this.organizeService.createMany(list);
                    } finally {
                        semaphore.release();
                    }
                }));
            }
            for (var future : futureList) {
                future.get();
            }
        }
    }

    private List<OrganizeModel> getMany(int pageSize) {
        var list = new ArrayList<OrganizeModel>();
        for (var i = pageSize; i > 0; i--) {
            list.add(new OrganizeModel().setName("Hello, World!"));
        }
        return list;
    }
}
