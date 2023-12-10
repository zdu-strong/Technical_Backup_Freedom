package com.springboot.project.common.permission;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import com.springboot.project.properties.AuthorizationEmailProperties;
import com.springboot.project.properties.IsTestOrDevModeProperties;

@Component
public class AuthorizationEmailUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private AuthorizationEmailProperties authorizationEmailProperties;

    @Autowired
    private IsTestOrDevModeProperties isTestOrDevModeProperties;

    public void sendVerificationCode(String email, String verificationCode) {
        if (this.isTestOrDevModeProperties.getIsTestOrDevMode()) {
            return;
        }

        this.sendEmail(email, verificationCode);
    }

    private void sendEmail(String email, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(this.authorizationEmailProperties.getSenderEmail());
            helper.setTo(email);
            helper.setSubject("Verification Code For Login");
            helper.setText(this.getEmailOfBody(verificationCode), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getEmailOfBody(String verificationCode) {
        try (InputStream input = new ClassPathResource("email/email.xml").getInputStream()) {
            String text = IOUtils.toString(input, StandardCharsets.UTF_8);
            String content = text.replaceAll(this.getRegex("verificationCode"), verificationCode);
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String getRegex(String name) {
        String regex = Pattern.quote("${") + "\\s*" + Pattern.quote(name) + "\\s*" + Pattern.quote("}");
        return regex;
    }

}
