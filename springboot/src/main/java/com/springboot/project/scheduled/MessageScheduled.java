package com.springboot.project.scheduled;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jinq.orm.stream.JinqStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.controller.UserMessageWebSocketController;

@Component
public class MessageScheduled {

    @Scheduled(initialDelay = 1000, fixedDelay = 1)
    public void scheduled() throws InterruptedException, ExecutionException {
        var websocketList = JinqStream.from(UserMessageWebSocketController.getStaticWebSocketList())
                .sortedBy(s -> s.getUserId()).toList();
        var futureList = new ArrayList<Future<?>>();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (var websocket : websocketList) {
                futureList.add(executor.submit(() -> {
                    websocket.sendMessage();
                }));
            }
        }
        for (var future : futureList) {
            future.get();
        }
    }

}
