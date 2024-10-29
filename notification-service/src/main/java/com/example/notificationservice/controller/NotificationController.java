package com.example.notificationservice.controller;

import com.example.notificationservice.dto.MailDTO;
import com.example.notificationservice.service.NotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping("send/purchase/{to}")
    public void sendPurchaseEmail(@RequestBody MailDTO mailDTO) throws MessagingException {
        service.sendPurchaseEmail(mailDTO);
    }

    @PostMapping("send/registration/{to}")
    public void sendRegistrationEmail(@RequestBody MailDTO mailDTO) throws MessagingException {
        service.sendRegistrationEmail(mailDTO);
    }

    @PostMapping("send/verification")
    public void sendProductVerificationEmail(@RequestBody MailDTO mailDTO) throws MessagingException {
        service.sendProductVerificationEmail(mailDTO);}

    @PostMapping("send/storage/update")
    public void sendUpdateStorageEmail(@RequestBody MailDTO mailDTO) throws MessagingException {
        service.sendUpdateStorageEmail(mailDTO);}
}
