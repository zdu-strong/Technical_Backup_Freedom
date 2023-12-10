package com.springboot.project.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.http.client.utils.URIBuilder;
import org.jinq.orm.stream.JinqStream;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.springboot.project.common.permission.PermissionUtil;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketReceiveModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.service.UserMessageService;
import lombok.Getter;
import lombok.Synchronized;

/**
 * Required parameters: String access_token;
 * 
 * @author zdu
 *
 */
@ServerEndpoint("/user_message/websocket")
@Component
public class UserMessageWebSocketController {

    /**
     * Autowired
     */
    private static UserMessageService _userMessageService;

    /**
     * Autowired
     */
    private static PermissionUtil _permissionUtil;

    /**
     * Autowired
     */
    private static ObjectMapper _objectMapper;

    /**
     * Public accessible properties
     */
    @Getter
    private final static CopyOnWriteArrayList<UserMessageWebSocketController> staticWebSocketList = new CopyOnWriteArrayList<UserMessageWebSocketController>();

    /**
     * Public accessible properties
     */
    @Getter
    private String userId;
    private String accessToken;
    private Session session;
    private CopyOnWriteArrayList<UserMessageModel> lastMessage = new CopyOnWriteArrayList<>();
    private ConcurrentMap<Long, UserMessageModel> onlineMessageMap = new ConcurrentHashMap<>();
    private boolean ready = false;

    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        _userMessageService = userMessageService;
    }

    @Autowired
    public void setPermissionUtil(PermissionUtil permissionUtil) {
        _permissionUtil = permissionUtil;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        _objectMapper = objectMapper;
    }

    /**
     * @param session
     * @param email
     * @throws InterruptedException
     */
    @OnOpen
    public void onOpen(Session session) throws URISyntaxException, IOException, InterruptedException {
        /**
         * Get access token
         */
        var accessToken = JinqStream.from(new URIBuilder(session.getRequestURI()).getQueryParams())
                .where(s -> s.getName().equals("accessToken")).select(s -> s.getValue()).getOnlyValue();
        _permissionUtil.checkIsSignIn(accessToken);
        var userId = _permissionUtil.getUserId(accessToken);
        /**
         * Save properties to member variables
         */
        this.userId = userId;
        this.session = session;
        this.accessToken = accessToken;
        staticWebSocketList.add(this);
    }

    @OnClose
    public void onClose() {
        staticWebSocketList.remove(this);
    }

    @OnError
    public void OnError(Session session, Throwable e) throws IOException {
        session
                .close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION,
                        CloseCodes.UNEXPECTED_CONDITION.name()));
    }

    /**
     * @param userMessageWebSocketReceiveModelString UserMessageWebSocketReceiveModel
     * @param session
     */
    @OnMessage
    public void OnMessage(String userMessageWebSocketReceiveModelString)
            throws IOException, InterruptedException {
        var userMessageWebSocketReceiveModel = _objectMapper.readValue(userMessageWebSocketReceiveModelString,
                UserMessageWebSocketReceiveModel.class);
        if (userMessageWebSocketReceiveModel.getIsCancel()) {
            this.onlineMessageMap.remove(userMessageWebSocketReceiveModel.getPageNum());
        } else {
            this.onlineMessageMap.put(userMessageWebSocketReceiveModel.getPageNum(), new UserMessageModel());
        }
    }

    public synchronized void sendMessage() {
        try {
            _permissionUtil.checkIsSignIn(accessToken);
            var messageList = _userMessageService.getMessageListByLastTwentyMessages(userId);
            {
                var newMessageList = JinqStream.from(messageList)
                        .where(s -> !JinqStream.from(this.lastMessage).anyMatch(t -> {
                            try {
                                var objectOne = new UserMessageModel();
                                var objectTwo = new UserMessageModel();
                                BeanUtils.copyProperties(s, objectOne, "totalPage");
                                BeanUtils.copyProperties(t, objectTwo, "totalPage");
                                return _objectMapper.writeValueAsString(objectOne).equals(
                                        _objectMapper.writeValueAsString(objectTwo));
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e.getMessage(), e);
                            }
                        })).toList();
                if (!this.ready || !newMessageList.isEmpty()
                        || (messageList.size() == 0 && this.lastMessage.size() != 0)) {
                    this.session.getBasicRemote()
                            .sendText(_objectMapper
                                    .writeValueAsString(new UserMessageWebSocketSendModel().setList(newMessageList)
                                            .setTotalPage(JinqStream.from(newMessageList).select(s -> s.getTotalPage())
                                                    .findFirst().orElse(0L))));
                    this.lastMessage.clear();
                    this.lastMessage.addAll(messageList);
                    this.ready = true;
                }
            }

            {
                for (var pageNum : this.onlineMessageMap.keySet()) {
                    if (messageList.stream().anyMatch(message -> message.getPageNum() == pageNum)) {
                        continue;
                    }

                    if (!this.onlineMessageMap.containsKey(pageNum)) {
                        continue;
                    }
                    var userMessageList = _userMessageService.getMessageListOnlyContainsOneByPageNum(pageNum,
                            this.userId);
                    if (userMessageList.isEmpty()) {
                        continue;
                    }
                    if (!this.onlineMessageMap.containsKey(pageNum)) {
                        continue;
                    }
                    var newMessageList = userMessageList.stream().filter(
                            s -> !Lists.newArrayList(this.onlineMessageMap.get(pageNum)).stream()
                                    .anyMatch(t -> {
                                        try {
                                            var objectOne = new UserMessageModel();
                                            var objectTwo = new UserMessageModel();
                                            BeanUtils.copyProperties(s, objectOne, "totalPage");
                                            BeanUtils.copyProperties(t, objectTwo, "totalPage");
                                            return _objectMapper.writeValueAsString(objectOne)
                                                    .equals(_objectMapper.writeValueAsString(objectTwo));
                                        } catch (JsonProcessingException e) {
                                            throw new RuntimeException(e.getMessage(), e);
                                        }
                                    }))
                            .toList();
                    if (newMessageList.isEmpty()) {
                        continue;
                    }
                    this.onlineMessageMap.put(pageNum, JinqStream.from(userMessageList).getOnlyValue());
                    this.session.getBasicRemote()
                            .sendText(_objectMapper.writeValueAsString(new UserMessageWebSocketSendModel()
                                    .setList(newMessageList).setTotalPage(null)));
                }
            }

        } catch (Throwable e) {
            try {
                this.session
                        .close(new CloseReason(CloseCodes.UNEXPECTED_CONDITION,
                                CloseCodes.UNEXPECTED_CONDITION.name()));
                throw new RuntimeException(e.getMessage(), e);
            } catch (IOException e1) {
                throw new RuntimeException(e1.getMessage(), e1);
            }
        }
    }
}
