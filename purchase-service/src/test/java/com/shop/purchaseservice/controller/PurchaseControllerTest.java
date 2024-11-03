package com.shop.purchaseservice.controller;

import com.shop.purchaseservice.dto.InventoryStatusDTO;
import com.shop.purchaseservice.dto.OrderWithProductCartDTO;
import com.shop.purchaseservice.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PurchaseController.class)
class PurchaseControllerTest {


    @Mock
    private PurchaseService service;

    private MockMvc mockMvc;

    public PurchaseControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPurchaseOperation() throws Exception {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();
        InventoryStatusDTO inventoryStatusDTO = new InventoryStatusDTO();
        inventoryStatusDTO.setIsOrderInStorage(true);

        when(service.purchase(orderDuplicateDTO)).thenReturn(inventoryStatusDTO);

        mockMvc.perform(post("/api/v1/purchase/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"customerId\":1,\"cost\":600,\"cart\":{}}"))
                .andExpect(status().isOk());

        verify(service, times(1)).purchase(orderDuplicateDTO);
    }

    @Test
    void testPurchaseMailSend() throws Exception {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();

        doNothing().when(service).purchaseMailSend(orderDuplicateDTO);

        mockMvc.perform(post("/api/v1/purchase/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"customerId\":1,\"cost\":600,\"cart\":{}}"))
                .andExpect(status().isOk());

        verify(service, times(1)).purchaseMailSend(orderDuplicateDTO);
    }
}
