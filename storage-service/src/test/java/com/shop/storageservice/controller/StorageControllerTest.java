package com.shop.storageservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.storageservice.dto.OrderWithProductCartDTO;
import com.shop.storageservice.dto.ProductDuplicateDTO;
import com.shop.storageservice.dto.ProductWithQuantityDTO;
import com.shop.storageservice.model.Storage;
import com.shop.storageservice.service.StorageService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(StorageController.class)
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StorageService storageService;

    private ProductDuplicateDTO productDuplicateDTO;
    private OrderWithProductCartDTO orderDuplicateDTO;
    private Storage storage;
    private ProductWithQuantityDTO productWithQuantityDTO;

    private Map<ProductDuplicateDTO, Integer> cart;

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

        cart = new HashMap<>();



        orderDuplicateDTO = OrderWithProductCartDTO.builder()
                .id("order1")
                .customerId("customer1")
                .cost(BigDecimal.valueOf(100))
                .build();

        storage = new Storage();

        productWithQuantityDTO = ProductWithQuantityDTO.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .cost(BigDecimal.valueOf(100))
                .producer("Test Producer")
                .category("Test Category")
                .feedBack(BigDecimal.valueOf(4.5))
                .quantity(5)
                .build();
    }

    @Test
    void testIsInStorage() throws Exception {
//        given
        when(storageService.isInStorage(anyLong(), anyInt())).thenReturn(true);

//        when
        mockMvc.perform(get("/api/v1/storage/check")
                        .param("id", "1")
                        .param("requiredQuantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

//        then
        verify(storageService, times(1)).isInStorage(1L, 10);
    }

    @Test
    void testSaveProduct() throws Exception {
//        given
        doNothing().when(storageService).saveProduct(anyInt(), any());

//        when
        mockMvc.perform(post("/api/v1/storage/save/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productDuplicateDTO)))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).saveProduct(10, productDuplicateDTO);
    }

    @Test
    void testUpdateProduct() throws Exception {
//        given
        doNothing().when(storageService).updateProduct(anyInt(), any());

//        when
        mockMvc.perform(put("/api/v1/storage/save/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productDuplicateDTO)))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).updateProduct(5, productDuplicateDTO);
    }

    @Test
    void testFindById() throws Exception {
//        given
        when(storageService.findById(anyLong())).thenReturn(storage);

//        when
        mockMvc.perform(get("/api/v1/storage/find/1"))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() throws Exception {
//        given
        doNothing().when(storageService).deleteById(anyLong());
//        when
        mockMvc.perform(delete("/api/v1/storage/delete/1"))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).deleteById(1L);
    }

    @Test
    void raiseProductQuantityById() throws Exception {
//        given
        doNothing().when(storageService).raiseProductQuantityById(any(), anyInt());

//        when
        mockMvc.perform(post("/api/v1/storage/add?quantityAdded=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productDuplicateDTO)))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).raiseProductQuantityById(productDuplicateDTO, 5);
    }

    @Test
    void testReduceQuantityById() throws Exception {
//        given
        doNothing().when(storageService).reduceQuantityById(any(OrderWithProductCartDTO.class));

//        when
        mockMvc.perform(put("/api/v1/storage/delete")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(orderDuplicateDTO)))
                .andExpect(status().isOk());

//        then
        verify(storageService, times(1)).reduceQuantityById(orderDuplicateDTO);
    }

    @Test
    void testIsOrderInStorage() throws Exception {
//        given
        when(storageService.isOrderInStorage(any())).thenReturn(true);

//        when
        mockMvc.perform(post("/api/v1/storage/check/order")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

//        then
        verify(storageService, times(1)).isOrderInStorage(cart);
    }

    @Test
    void testFindOutOfStorageProduct() throws Exception {
//        given
        Map<ProductDuplicateDTO, Integer> expectedResponse = new HashMap<>();
        when(storageService.findOutOfStorageProduct(cart, "customer1")).thenReturn(expectedResponse);

//        when
        mockMvc.perform(post("/api/v1/storage/find/order/out/customerId")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(cart)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));

//        then
        verify(storageService, times(1)).findOutOfStorageProduct(cart, "customerId");
    }

    @Test
    void testFindAllStorageWithQuantity() throws Exception {
//        givem
        List<ProductWithQuantityDTO> expectedResponse = List.of(productWithQuantityDTO);
        when(storageService.findAllStorageWithQuantity()).thenReturn(expectedResponse);

//        when
        mockMvc.perform(get("/api/v1/storage/find/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(expectedResponse)));

//        then
        verify(storageService, times(1)).findAllStorageWithQuantity();
    }
}

