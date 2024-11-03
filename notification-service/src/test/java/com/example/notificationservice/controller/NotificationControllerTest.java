package com.example.notificationservice.controller;

import com.example.notificationservice.dto.MailDTO;
import com.example.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

@WebMvcTest(NotificationController.class)
public class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    private MailDTO mailDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mailDTO = MailDTO.builder()
                .to("test@example.com")
                .data(new HashMap<String, Object>())
                .build();
    }

    @Test
    public void testSendPurchaseEmail() throws Exception {
        doNothing().when(service).sendPurchaseEmail(mailDTO);

        mockMvc.perform(post("/api/v1/notification/send/purchase/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mailDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendRegistrationEmail() throws Exception {
        doNothing().when(service).sendRegistrationEmail(mailDTO);

        mockMvc.perform(post("/api/v1/notification/send/registration/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mailDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendProductVerificationEmail() throws Exception {
        doNothing().when(service).sendProductVerificationEmail(mailDTO);

        mockMvc.perform(post("/api/v1/notification/send/verification")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mailDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testSendUpdateStorageEmail() throws Exception {
        doNothing().when(service).sendUpdateStorageEmail(mailDTO);

        mockMvc.perform(post("/api/v1/notification/send/storage/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(mailDTO)))
                .andExpect(status().isOk());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
