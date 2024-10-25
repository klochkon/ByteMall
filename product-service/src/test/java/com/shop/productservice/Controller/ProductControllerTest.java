package com.shop.productservice.Controller;

import com.shop.productservice.DTO.ProductWithQuantityDTO;
import com.shop.productservice.Model.Product;
import com.shop.productservice.Service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProductControllerTest {

    @Mock
    private ProductService service;

    @InjectMocks
    private ProductController controller;

    private MockMvc mockMvc;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        product = Product.builder()
                .id(1L)
                .name("Product 1")
                .category("Electronics")
                .cost(BigDecimal.valueOf(499.99))
                .description("Description")
                .slug("product-1")
                .producer("Producer 1")
                .feedBack(BigDecimal.valueOf(4.5))
                .build();
    }

    @Test
    void getAllProductWithQuantity() throws Exception {
        List<ProductWithQuantityDTO> productWithQuantityList = new ArrayList<>();

        when(service.getAllProductWithQuantity(any())).thenReturn(productWithQuantityList);

        mockMvc.perform(get("/api/v1/product/get/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]")) // Пустий JSON для тіла запиту
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createProduct() throws Exception {
        when(service.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/v1/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Product 1\", \"category\":\"Electronics\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    @Test
    void updateProduct() throws Exception {
        when(service.updateProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(put("/api/v1/product/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1, \"name\":\"Updated Product\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void deleteProductById() throws Exception {
        mockMvc.perform(delete("/api/v1/product/delete/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void shareProduct() throws Exception {
        when(service.findBySlug(anyString())).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/share/{slug}", "product-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("product-1"))
                .andExpect(jsonPath("$.name").value("Product 1"));
    }

    @Test
    void getProductById() throws Exception {
        when(service.findById(anyLong())).thenReturn(product);

        mockMvc.perform(get("/api/v1/product/get/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Product 1"));
    }

    @Test
    void findProductByCategory() throws Exception {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(service.findAllByCategory(anyString())).thenReturn(productList);

        mockMvc.perform(get("/api/v1/product/get/category/{category}", "Electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("Electronics"))
                .andExpect(jsonPath("$[0].name").value("Product 1"));
    }
}
