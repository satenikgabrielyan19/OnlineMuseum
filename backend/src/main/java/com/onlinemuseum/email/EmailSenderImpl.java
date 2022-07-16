package com.onlinemuseum.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailSenderImpl implements EmailSender {
    private final JavaMailSender mailSender;
    private final Logger logger = LoggerFactory.getLogger(EmailSenderImpl.class);

    @Override
    @Async
    public void send(String to, String email, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,
                    "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("narektm.developer@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            logger.error("Fail to send email.\n", e);
            throw new IllegalStateException("Failed to send.");
        }
    }

}
