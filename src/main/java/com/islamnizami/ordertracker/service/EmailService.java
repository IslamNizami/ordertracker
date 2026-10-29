package com.islamnizami.ordertracker.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Async
    @Retryable(
            retryFor = { MailException.class, Exception.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendOrderStatusEmail(String toEmail, Long orderId, String newStatus) {
        logger.info("Sending order status email to {} for Order #{} (Status: {})", toEmail, orderId, newStatus);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Order #" + orderId + " Status Update");
        message.setText(String.format(
                "Hello,\n\nYour order #%d status has been updated to: %s.\n\nThank you for shopping with us!",
                orderId, newStatus
        ));

        mailSender.send(message);
        logger.info("Email sent successfully to {}", toEmail);
    }

    @Recover
    public void recoverFailedEmail(Exception e, String toEmail, Long orderId, String newStatus) {
        logger.error("All retry attempts failed for sending email to {} for Order #{}. Error: {}",
                toEmail, orderId, e.getMessage());
    }
}