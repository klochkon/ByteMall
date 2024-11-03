package com.shop.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.service.SaleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
public class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService service;

    @Autowired
    private ObjectMapper objectMapper;

    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = Sale.builder()
                .id(1L)
                .customerId(123L)
                .sale(new BigDecimal("100.0"))
                .build();
    }

    @Test
    void testUpdateSale() throws Exception {
        when(service.updateSale(any(Sale.class))).thenReturn(sale);

        mockMvc.perform(put("/api/v1/sale/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sale)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$.sale").value(sale.getSale().toString()));

        verify(service).updateSale(any(Sale.class));
    }

    @Test
    void testSaveSale() throws Exception {
        when(service.saveSale(any(Sale.class))).thenReturn(sale);

        mockMvc.perform(post("/api/v1/sale/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sale)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$.sale").value(sale.getSale().toString()));

        verify(service).saveSale(any(Sale.class));
    }

    @Test
    void testDeleteSaleById() throws Exception {
        Long saleId = sale.getId();

        mockMvc.perform(delete("/api/v1/sale/delete/{id}", saleId))
                .andExpect(status().isOk());

        verify(service).deleteSaleById(eq(saleId));
    }

    @Test
    void testFindSaleById() throws Exception {
        Long saleId = sale.getId();
        when(service.findSaleById(saleId)).thenReturn(sale);

        mockMvc.perform(get("/api/v1/sale/find/{id}", saleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sale.getId()))
                .andExpect(jsonPath("$.customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$.sale").value(sale.getSale().toString()));

        verify(service).findSaleById(eq(saleId));
    }

    @Test
    void testFindAllByCustomerId() throws Exception {
        Long customerId = sale.getCustomerId();
        when(service.findAllByCustomerId(customerId)).thenReturn(List.of(sale));

        mockMvc.perform(get("/api/v1/sale/find/all/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sale.getId()))
                .andExpect(jsonPath("$[0].customerId").value(sale.getCustomerId()))
                .andExpect(jsonPath("$[0].sale").value(sale.getSale().toString()));

        verify(service).findAllByCustomerId(eq(customerId));
    }
}
