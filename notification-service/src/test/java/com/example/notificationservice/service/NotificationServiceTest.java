package com.example.notificationservice.service;

import com.example.notificationservice.dto.MailDTO;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender sender;

    @Mock
    private TemplateEngine templateEngine;

    @Spy
    @InjectMocks
    private NotificationService notificationService;

    private MailDTO mailDTO;

    @BeforeEach
    void setUp() {
        mailDTO = new MailDTO();
        mailDTO.setTo("test@example.com");
        mailDTO.setData(Map.of("key", "value"));
    }

    @Test
    void testSendEmail() throws Exception {
        String template = "someTemplate";
        String subject = "Test Subject";

        when(sender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(sender).send(any(MimeMessage.class));
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html");

        notificationService.sendEmail(mailDTO, template, subject);

        verify(sender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendPurchaseEmail() throws Exception {
        String purchaseSubject = "purchaseSubject";
        notificationService.setPurchaseSubject(purchaseSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());
        notificationService.sendPurchaseEmail(mailDTO);

        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((purchaseSubject)));
    }

    @Test
    void testSendRegistrationEmail() throws Exception {
        String registrationSubject = "registrationSubject";
        notificationService.setRegistrationSubject(registrationSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());
        notificationService.sendRegistrationEmail(mailDTO);

        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((registrationSubject)));
    }

    @Test
    void testSendProductVerificationEmail() throws Exception {
        String verificationSubject = "verificationSubject";
        String storageAdminEmail = "storageAdminEmail";

        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());

        notificationService.setStorageAdminEmail(storageAdminEmail);
        notificationService.setRegistrationSubject(verificationSubject);
        notificationService.sendProductVerificationEmail(mailDTO);

        MailDTO expectedMailDTO = new MailDTO();
        expectedMailDTO.setData(mailDTO.getData());
        expectedMailDTO.setTo(storageAdminEmail);

        verify(notificationService, times(1)).sendEmail(eq(expectedMailDTO), anyString(), anyString());
    }

    @Test
    void testSendUpdateStorageEmail() throws Exception {
        String updateStorageSubject = "updateStorageSubject";
        notificationService.setUpdateStorageSubject(updateStorageSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());
        notificationService.sendUpdateStorageEmail(mailDTO);

        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((updateStorageSubject)));
    }
}
