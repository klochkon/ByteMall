package com.example.notificationservice.service;

import com.example.notificationservice.dto.MailDTO;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock
    private JavaMailSender sender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @Mock
    private MimeMessageHelper mimeMessageHelper;

    private MailDTO mailDTO;
    private String template;
    private String subject;

    @BeforeEach
    void setUp() throws Exception {

        mailDTO = new MailDTO();
        mailDTO.setTo("test@example.com");
        mailDTO.setData(Map.of("key", "value"));

        template = "someTemplate";
        subject = "Test Subject";

        when(sender.createMimeMessage()).thenReturn(mimeMessage);

    }

    @Test
    void sendEmailTest() throws MessagingException {
        when(templateEngine.process(eq(template), any())).thenReturn("<html>Test Email</html>");

        notificationService.sendEmail(mailDTO, template, subject);

        verify(mimeMessageHelper).setTo(mailDTO.getTo());
        verify(mimeMessageHelper).setSubject(subject);
        verify(mimeMessageHelper).setText("<html>Test Email</html>", true);
        verify(sender).send(mimeMessage);
    }

    @Test
    void sendPurchaseEmailTest() throws MessagingException {
        doNothing().when(sendEmail(any(), any(), any()));

        notificationService.sendPurchaseEmail(mailDTO);

        verify(sender).send(mimeMessage);
    }

    @Test
    void sendRegistrationEmailTest() throws MessagingException {
        when(templateEngine.process(eq(template), any())).thenReturn("<html>Registration Email</html>");

        notificationService.sendRegistrationEmail(mailDTO);

        verify(sender).send(mimeMessage);
    }

    @Test
    void sendProductVerificationEmailTest() throws MessagingException {
        String adminEmail = "admin@example.com";
        notificationService.setStorageAdminEmail(adminEmail);

        when(templateEngine.process(eq(template), any())).thenReturn("<html>Verification Email</html>");

        notificationService.sendProductVerificationEmail(mailDTO);

        ArgumentCaptor<MailDTO> mailArgumentCaptor = ArgumentCaptor.forClass(MailDTO.class);
        verify(sender).send(any());
        assertEquals(adminEmail, mailArgumentCaptor.getValue().getTo());
    }

    @Test
    void sendUpdateStorageEmailTest() throws MessagingException {
        when(templateEngine.process(eq(template), any())).thenReturn("<html>Update Storage Email</html>");

        notificationService.sendUpdateStorageEmail(mailDTO);

        verify(sender).send(mimeMessage);
    }
}
