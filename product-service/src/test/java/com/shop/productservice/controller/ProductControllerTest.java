package com.shop.productservice.controller;

import com.shop.productservice.dto.*;
import com.shop.productservice.model.Product;
import com.shop.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ProductService service;

    private Product product;
    private MockMultipartFile photo;

    @BeforeEach
    void setUp() throws IOException {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .cost(new BigDecimal("100.0"))
                .description("Test Description")
                .build();

        photo = new MockMultipartFile("photo", "test.jpg", "image/jpeg", new ByteArrayInputStream("test".getBytes()));
    }

    @Test
    void getAllProductWithQuantity() throws Exception {
        when(service.getAllProductWithQuantity(any())).thenReturn(Collections.singletonList(new ProductWithQuantityDTO()));

        mockMvc.perform(post("/api/v1/product/get/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")) // Sending an empty JSON array
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void groupNameIdentifier() throws Exception {
        when(service.groupNameIdentifier(any())).thenReturn(Collections.singletonList(new OrderWithProductCartDTO()));

        mockMvc.perform(post("/api/v1/product/name-identifier/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")) // Sending an empty JSON array
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void createProduct() throws Exception {
        when(service.createProduct(any(), any())).thenReturn(product);

        mockMvc.perform(multipart("/api/v1/product/create")
                        .file(photo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product))) // Sending the product as JSON
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    void updateProduct() throws Exception {
        when(service.updateProduct(any(), any())).thenReturn(product);

        mockMvc.perform(multipart("/api/v1/product/update")
                        .file(photo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product))) // Sending the product as JSON
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    void deleteProductById() throws Exception {
        doNothing().when(service).deleteById(anyLong());

        mockMvc.perform(delete("/api/v1/product/delete/1"))
                .andExpect(status().isOk());

        verify(service).deleteById(1L);
    }

    @Test
    void nameIdentifier() throws Exception {
        when(service.nameIdentifier(any())).thenReturn(Collections.singletonList(new ProductDuplicateDTO()));

        mockMvc.perform(post("/api/v1/product/name-identifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[1]")) // Sending a JSON array with one ID
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void shareProduct() throws Exception {
        when(service.findBySlug(any())).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/share/test-slug"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    void getProductById() throws Exception {
        when(service.findById(anyLong())).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(product.getId()));
    }

    @Test
    void findProductByCategory() throws Exception {
        when(service.findAllByCategory(any())).thenReturn(Collections.singletonList(product));

        mockMvc.perform(get("/api/v1/product/get/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].id").value(product.getId()));
    }
}
