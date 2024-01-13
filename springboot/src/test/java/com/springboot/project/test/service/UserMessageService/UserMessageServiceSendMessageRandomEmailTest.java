package com.springboot.project.test.service.UserMessageService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.net.URISyntaxException;
import com.fasterxml.uuid.Generators;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.common.BaseTest.BaseTest;

public class UserMessageServiceSendMessageRandomEmailTest extends BaseTest {
    private UserMessageModel userMessage;

    @Test
    public void test() throws URISyntaxException {
        var message = this.userMessageService.sendMessage(userMessage);
        assertEquals(36, message.getId().length());
        assertEquals("Hello, World!", message.getContent());
        assertEquals(this.userMessage.getUser().getId(), message.getUser().getId());
        assertFalse(message.getIsRecall());
        assertFalse(message.getIsDeleted());
    }

    @BeforeEach
    public void beforeEach() {
        var userId = this.createAccount(Generators.timeBasedGenerator().generate().toString() + "zdu.strong@gmail.com")
                .getId();
        this.userMessage = new UserMessageModel().setUser(new UserModel().setId(userId)).setContent("Hello, World!");
    }
}
