package com.springboot.project.scheduled;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.jinq.orm.stream.JinqStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.springboot.project.controller.UserMessageWebSocketController;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageScheduled {

    @Scheduled(initialDelay = 1000, fixedDelay = 1)
    public void scheduled() throws InterruptedException, ExecutionException {
        var websocketList = JinqStream.from(UserMessageWebSocketController.getStaticWebSocketList())
                .sortedBy(s -> s.getUserId()).toList();
        CompletableFuture.allOf(JinqStream.from(websocketList).select(websocket -> CompletableFuture.runAsync(() -> {
            try {
                websocket.sendMessage();
            } catch (Throwable e) {
                log.error("Failed to send latest message for UserId \"" + websocket.getUserId() + "\"", e);
            }
        })).toList().toArray(new CompletableFuture[] {})).get();
    }

}
