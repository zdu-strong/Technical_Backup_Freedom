package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceGetUserMessageByIdTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() throws URISyntaxException {
        var result = this.userMessageService.getUserMessageById(this.userMessage.getId(),
                this.userMessage.getUser().getId());
        assertEquals(this.userMessage.getId(), result.getId());
        assertFalse(result.getIsRecall());
        assertEquals("Hello, World!", result.getContent());
        assertNotNull(result.getCreateDate());
        assertFalse(result.getIsDeleted());
        assertFalse(result.getIsRecall());
        assertNull(result.getPageNum());
        assertNull(result.getTotalPage());
        assertNotNull(result.getUpdateDate());
        assertNull(result.getUrl());
        assertTrue(StringUtils.isNotBlank(result.getUser().getId()));
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
