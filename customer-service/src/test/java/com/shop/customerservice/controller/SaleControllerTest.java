package com.shop.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.service.SaleService;
import org.bson.types.ObjectId;
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
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SaleController.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SaleService saleService;

    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = Sale.builder()
                .id(new ObjectId())
                .customerId("101")
                .sale(new BigDecimal(10))
                .build();
    }

    @Test
    void testUpdateSale() throws Exception {
        when(saleService.updateSale(any(Sale.class))).thenReturn(sale);

        mockMvc.perform(put("/api/v1/sale/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sale)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("101"))
                .andExpect(jsonPath("$.sale").value(10));

        verify(saleService, times(1)).updateSale(any(Sale.class));
    }

    @Test
    void testSaveSale() throws Exception {
        when(saleService.saveSale(any(Sale.class))).thenReturn(sale);

        mockMvc.perform(post("/api/v1/sale/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(sale)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("101"))
                .andExpect(jsonPath("$.sale").value(10));

        verify(saleService, times(1)).saveSale(any(Sale.class));
    }

    @Test
    void testDeleteSaleById() throws Exception {
        doNothing().when(saleService).deleteSaleById(anyString());

        mockMvc.perform(delete("/api/v1/sale/delete/1"))
                .andExpect(status().isOk());

        verify(saleService, times(1)).deleteSaleById("1");
    }

    @Test
    void testFindSaleById() throws Exception {
        when(saleService.findSaleById(anyString())).thenReturn(sale);

        mockMvc.perform(get("/api/v1/sale/find/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.customerId").value("101"))
                .andExpect(jsonPath("$.sale").value(10));

        verify(saleService, times(1)).findSaleById(anyString());
    }

    @Test
    void testFindAllByCustomerId() throws Exception {
        List<Sale> sales = List.of(sale);
        when(saleService.findAllByCustomerId(anyString())).thenReturn(sales);

        mockMvc.perform(get("/api/v1/sale/find/all/202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].customerId").value("101"))
                .andExpect(jsonPath("$[0].sale").value(10));

        verify(saleService, times(1)).findAllByCustomerId("202");
    }
}
