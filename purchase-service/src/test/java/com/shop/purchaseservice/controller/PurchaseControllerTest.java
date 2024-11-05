package com.shop.purchaseservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.purchaseservice.dto.InventoryStatusDTO;
import com.shop.purchaseservice.dto.OrderWithProductCartDTO;
import com.shop.purchaseservice.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {


    @MockBean
    private PurchaseService service;

    @Autowired
    private MockMvc mockMvc;

    private OrderWithProductCartDTO orderWithProductCartDTO;

    @BeforeEach
    void setUp() {
        orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                .id("id")
                .customerId("customerId")
                .cost(new BigDecimal(1))
                .build();
    }

    @Test
    void testPurchaseOperation() throws Exception {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();
        InventoryStatusDTO expectedInventoryStatusDTO = new InventoryStatusDTO();
        expectedInventoryStatusDTO.setIsOrderInStorage(true);

        when(service.purchase(orderDuplicateDTO)).thenReturn(expectedInventoryStatusDTO);


        mockMvc.perform(post("/api/v1/purchase/operation")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(orderWithProductCartDTO)))
                .andExpect(status().isOk());

        verify(service, times(1)).purchase(orderWithProductCartDTO);
    }

    @Test
    void testPurchaseMailSend() throws Exception {
        doNothing().when(service).purchaseMailSend(orderWithProductCartDTO);

        mockMvc.perform(post("/api/v1/purchase/mail/send")
                        .contentType("application/json")
                        .content(new ObjectMapper().writeValueAsString(orderWithProductCartDTO)))
                .andExpect(status().isOk());

        verify(service, times(1)).purchaseMailSend(orderWithProductCartDTO);
    }
}
