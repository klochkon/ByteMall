package com.example.notificationservice.controller;

import com.example.notificationservice.dto.MailDTO;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    private MailDTO mailDTO;

    @BeforeEach
    public void setUp() {

        mailDTO = MailDTO.builder()
                .to("test@example.com")
                .data(new HashMap<String, Object>())
                .build();
    }

    @Test
    public void testSendPurchaseEmail() throws Exception {
//        given
        doNothing().when(service).sendPurchaseEmail(mailDTO);

//        when
        mockMvc.perform(post("/api/v1/notification/send/purchase/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mailDTO)))
                .andExpect(status().isOk());

//        then
        verify(service, times(1)).sendPurchaseEmail(mailDTO);
    }

    @Test
    public void testSendRegistrationEmail() throws Exception {
//        given
        doNothing().when(service).sendRegistrationEmail(mailDTO);

//        when
        mockMvc.perform(post("/api/v1/notification/send/registration/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mailDTO)))
                .andExpect(status().isOk());

//        then
        verify(service, times(1)).sendRegistrationEmail(mailDTO);
    }

    @Test
    public void testSendProductVerificationEmail() throws Exception {
//        given
        doNothing().when(service).sendProductVerificationEmail(mailDTO);

//        when
        mockMvc.perform(post("/api/v1/notification/send/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mailDTO)))
                .andExpect(status().isOk());

//        then
        verify(service, times(1)).sendProductVerificationEmail(mailDTO);
    }

    @Test
    public void testSendUpdateStorageEmail() throws Exception {
//         given
        doNothing().when(service).sendUpdateStorageEmail(mailDTO);

//        when
        mockMvc.perform(post("/api/v1/notification/send/storage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(mailDTO)))
                .andExpect(status().isOk());

//        then
        verify(service, times(1)).sendUpdateStorageEmail(mailDTO);
    }
}
