package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.StringUtils;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetMessageListByLastTwentyMessagesTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() throws URISyntaxException {
        var messageList = this.userMessageService.getMessageListByLastTwentyMessages(
                this.userMessage.getUser().getId());
        var message = JinqStream.from(messageList).where(s -> s.getId().equals(this.userMessage.getId()))
                .getOnlyValue();
        assertTrue(messageList.size() >= 1);
        assertTrue(StringUtils.isNotBlank(message.getId()));
        assertFalse(message.getIsRecall());
        assertTrue(StringUtils.isNotBlank(message.getContent()));
        assertNotNull(message.getCreateDate());
        assertFalse(message.getIsDeleted());
        assertTrue(message.getPageNum() >= 1);
        assertTrue(message.getTotalPage() >= 1);
        assertNotNull(message.getUpdateDate());
        assertNull(message.getUrl());
        assertTrue(StringUtils.isNotBlank(message.getUser().getId()));
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getId();
        var userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
        var message = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(userId, message.getUser().getId());
        assertFalse(message.getIsRecall());
        assertFalse(message.getIsDeleted());
        this.userMessage = message;
    }
}
