package com.springboot.project.test.scheduled.MessageScheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;
import io.reactivex.rxjava3.subjects.ReplaySubject;
import jakarta.websocket.CloseReason.CloseCodes;

public class MessageScheduledTest extends BaseTest {

    private UserModel user;
    private ReplaySubject<UserMessageWebSocketSendModel> subject;
    private WebSocketClient webSocketClient;

    @Test
    public void test() throws InterruptedException, ExecutionException, TimeoutException {
        this.messageScheduled.scheduled();
        var result = this.subject.take(1).toList().toFuture().get(10, TimeUnit.SECONDS);
        assertEquals(1, result.size());
        assertTrue(JinqStream.from(result).selectAllList(s -> s.getList()).count() > 0);
        assertTrue(JinqStream.from(result).select(s -> s.getTotalPage()).findFirst().get() > 0);
        assertEquals("Hello, World!", JinqStream.from(result).selectAllList(s -> s.getList())
                .where(s -> s.getUser().getId().equals(this.user.getId())).select(s -> s.getContent())
                .limit(1)
                .getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException,
            JsonProcessingException {
        var webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        this.user = this.createAccount("zdu.strong@gmail.com");
        var accessToken = this.user.getAccess_token();
        var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage);
        URI url = new URIBuilder(webSocketServer).setPath("/user_message/websocket")
                .setParameter("accessToken", accessToken)
                .build();
        this.subject = ReplaySubject.create(1);
        this.webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {

            }

            @Override
            public void onMessage(String message) {
                try {
                    subject.onNext(objectMapper.readValue(message, UserMessageWebSocketSendModel.class));
                } catch (JsonMappingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (code == CloseCodes.NORMAL_CLOSURE.getCode()) {
                    subject.onComplete();
                } else {
                    subject.onError(new RuntimeException(reason));
                }
            }

            @Override
            public void onError(Exception ex) {
                subject.onError(ex);
            }
        };
        this.webSocketClient.connectBlocking();
    }

    @AfterEach
    public void afterEach() throws InterruptedException {
        this.webSocketClient.closeBlocking();
    }

}
