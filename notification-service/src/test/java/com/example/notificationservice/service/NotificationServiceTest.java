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
//        given
        String template = "someTemplate";
        String subject = "Test Subject";

        when(sender.createMimeMessage()).thenReturn(mock(MimeMessage.class));
        doNothing().when(sender).send(any(MimeMessage.class));
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("html");

//        when
        notificationService.sendEmail(mailDTO, template, subject);

//        then
        verify(sender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendPurchaseEmail() throws Exception {
//        given
        String purchaseSubject = "purchaseSubject";
        notificationService.setPurchaseSubject(purchaseSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());

//        when
        notificationService.sendPurchaseEmail(mailDTO);

//        then
        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((purchaseSubject)));
    }

    @Test
    void testSendRegistrationEmail() throws Exception {
//        given
        String registrationSubject = "registrationSubject";
        notificationService.setRegistrationSubject(registrationSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());

//        when
        notificationService.sendRegistrationEmail(mailDTO);

//        then
        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((registrationSubject)));
    }

    @Test
    void testSendProductVerificationEmail() throws Exception {
//        given
        String verificationSubject = "verificationSubject";
        String storageAdminEmail = "storageAdminEmail";
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), eq("someTemplate"), eq(verificationSubject));
        notificationService.setStorageAdminEmail(storageAdminEmail);
        notificationService.setVerificationSubject(verificationSubject);

//        when
        notificationService.sendProductVerificationEmail(mailDTO);

//        then
        verify(notificationService, times(1)).sendEmail(any(MailDTO.class), eq("someTemplate"), eq(verificationSubject));
    }

    @Test
    void testSendUpdateStorageEmail() throws Exception {
//        given
        String updateStorageSubject = "updateStorageSubject";
        notificationService.setUpdateStorageSubject(updateStorageSubject);
        doNothing().when(notificationService).sendEmail(any(MailDTO.class), anyString(), anyString());

//        when
        notificationService.sendUpdateStorageEmail(mailDTO);

//        then
        verify(notificationService, times(1)).sendEmail(eq(mailDTO), anyString(), eq((updateStorageSubject)));
    }
}
