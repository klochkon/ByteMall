package com.shop.purchaseservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.purchaseservice.dto.InventoryStatusDTO;
import com.shop.purchaseservice.dto.OrderWithProductCartDTO;
import com.shop.purchaseservice.dto.ProductDuplicateDTO;
import com.shop.purchaseservice.service.PurchaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {


    @Mock
    private PurchaseService service;

    @Mock
    private MockMvc mockMvc;

    private OrderWithProductCartDTO orderWithProductCartDTO;

    private ProductDuplicateDTO productDuplicateDTO;

    @BeforeEach
    void setUp() {
        productDuplicateDTO = ProductDuplicateDTO.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .cost(BigDecimal.valueOf(100))
                .producer("Test Producer")
                .category("Test Category")
                .feedBack(BigDecimal.valueOf(4.5))
                .build();

        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 1);
        OrderWithProductCartDTO orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                .id("id")
                .customerId("customerId")
                .cost(new BigDecimal(1))
                .cart(cart)
                .build();
    }

    @Test
    void testPurchaseOperation() throws Exception {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();
        InventoryStatusDTO expectedInventoryStatusDTO = new InventoryStatusDTO();
        expectedInventoryStatusDTO.setIsOrderInStorage(true);

        when(service.purchase(orderDuplicateDTO)).thenReturn(expectedInventoryStatusDTO);


        mockMvc.perform(post("/api/v1/purchase/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDuplicateDTO)))
                .andExpect(status().isOk());

        verify(service, times(1)).purchase(orderDuplicateDTO);
    }

    @Test
    void testPurchaseMailSend() throws Exception {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();

        doNothing().when(service).purchaseMailSend(orderDuplicateDTO);

        mockMvc.perform(post("/api/v1/purchase/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDuplicateDTO)))
                .andExpect(status().isOk());

        verify(service, times(1)).purchaseMailSend(orderDuplicateDTO);
    }
}
