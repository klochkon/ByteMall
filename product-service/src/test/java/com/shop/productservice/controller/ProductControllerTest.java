package com.shop.productservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.productservice.dto.*;
import com.shop.productservice.model.Product;
import com.shop.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product product;

    private List<MultipartFile> photos;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .cost(new BigDecimal("100.0"))
                .description("Test Description")
                .build();

        MockMultipartFile photo1 = new MockMultipartFile(
                "photos",
                "photo1.jpg",
                "image/jpeg",
                "Test Image Content 1".getBytes()
        );

        MockMultipartFile photo2 = new MockMultipartFile(
                "photos",
                "photo2.jpg",
                "image/jpeg",
                "Test Image Content 2".getBytes()
        );

        photos = List.of(photo1, photo2);
    }

    @Test
    void testGetAllProductWithQuantity() throws Exception {
//        given
        List<ProductWithQuantityDTO> products = List.of(new ProductWithQuantityDTO());
        when(productService.getAllProductWithQuantity(any())).thenReturn(products);

//        when
        mockMvc.perform(post("/api/v1/product/get/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of(new StorageDuplicateDTO()))))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(products)));

//        then
        verify(productService, times(1)).getAllProductWithQuantity(any());
    }

    @Test
    void testGroupNameIdentifier() throws Exception {
//        given
        List<OrderWithProductCartDTO> orderList = List.of(new OrderWithProductCartDTO());
        when(productService.groupNameIdentifier(any())).thenReturn(orderList);

//        when
        mockMvc.perform(post("/api/v1/product/name-identifier/group")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of(new OrderDuplicateDTO()))))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderList)));

//        then
        verify(productService, times(1)).groupNameIdentifier(any());
    }

    @Test
    void testCreateProduct() throws Exception {
//        given
        when(productService.createProduct(any(Product.class), any())).thenReturn(product);

//        when
        mockMvc.perform(post("/api/v1/product/create")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(product)));

//        then
        verify(productService, times(1)).createProduct(any(Product.class), any());
    }

    @Test
    void testUpdateProduct() throws Exception {
//        given
        when(productService.updateProduct(any(Product.class), any())).thenReturn(product);

//        when
        mockMvc.perform(put("/api/v1/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(photos))
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(product)));

//        then
        verify(productService, times(1)).updateProduct(any(Product.class), any());
    }

    @Test
    void testDeleteProductById() throws Exception {
//        given
        doNothing().when(productService).deleteById(anyLong());

//        when
        mockMvc.perform(delete("/api/v1/product/delete/1"))
                .andExpect(status().isOk());

//        then
        verify(productService, times(1)).deleteById(1L);
    }

    @Test
    void testNameIdentifier() throws Exception {
//        given
        List<ProductDuplicateDTO> duplicates = List.of(new ProductDuplicateDTO());
        when(productService.nameIdentifier(any())).thenReturn(duplicates);

//        when
        mockMvc.perform(post("/api/v1/product/name-identifier")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(List.of(1L, 2L))))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(duplicates)));

//        then
        verify(productService, times(1)).nameIdentifier(any());
    }

    @Test
    void testShareProduct() throws Exception {
//        given
        when(productService.findBySlug(anyString())).thenReturn(product);

//        when
        mockMvc.perform(get("/api/v1/product/share/test-slug"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(product)));

//        then
        verify(productService, times(1)).findBySlug("test-slug");
    }

    @Test
    void testGetProductById() throws Exception {
//        given
        when(productService.findById(anyLong())).thenReturn(product);

//        when
        mockMvc.perform(get("/api/v1/product/get/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(product)));

//        then
        verify(productService, times(1)).findById(1L);
    }

    @Test
    void testFindProductByCategory() throws Exception {
//        given
        List<Product> products = List.of(product);
        when(productService.findAllByCategory(anyString())).thenReturn(products);

//        when
        mockMvc.perform(get("/api/v1/product/get/category/Electronics"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(products)));

//        then
        verify(productService, times(1)).findAllByCategory("Electronics");
    }
}
