package com.shop.customerservice.controller;

import com.shop.customerservice.dto.CustomerDTO;
import com.shop.customerservice.dto.CustomerWithCartDTO;
import com.shop.customerservice.model.Customer;
import com.shop.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CustomerService service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    void saveCustomer() throws Exception {
        when(service.saveCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(post("/api/v1/customer/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(service, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void updateCustomer() throws Exception {
        when(service.updateCustomer(any(Customer.class))).thenReturn(customer);

        mockMvc.perform(put("/api/v1/customer/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(service, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void deleteCustomer() throws Exception {
        doNothing().when(service).deleteCustomerById(anyLong());

        mockMvc.perform(delete("/api/v1/customer/delete/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).deleteCustomerById(1L);
    }

    @Test
    void findCustomerById() throws Exception {
        CustomerWithCartDTO customerWithCartDTO = new CustomerWithCartDTO();
        when(service.findCustomerById(anyLong())).thenReturn(customerWithCartDTO);

        mockMvc.perform(get("/api/v1/customer/find/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).findCustomerById(1L);
    }

    @Test
    void findCustomerEmailAndNameById() throws Exception {
        CustomerDTO customerDTO = new CustomerDTO();
        when(service.findCustomerEmailAndNameById(anyLong())).thenReturn(customerDTO);

        mockMvc.perform(get("/api/v1/customer/find/customerDTO/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).findCustomerEmailAndNameById(1L);
    }

    @Test
    void findAllCustomer() throws Exception {
        when(service.findAllCustomer()).thenReturn(Collections.singletonList(customer));

        mockMvc.perform(get("/api/v1/customer/find/all"))
                .andExpect(status().isOk());

        verify(service, times(1)).findAllCustomer();
    }

    @Test
    void cleanCart() throws Exception {
        doNothing().when(service).cleanCart(anyString());

        mockMvc.perform(put("/api/v1/customer/clean/cart/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).cleanCart("1");
    }

    @Test
    void customerIdentify() throws Exception {
        Map<Long, String> productsMap = new HashMap<>();
        productsMap.put(1L, "Product 1");

        mockMvc.perform(put("/api/v1/customer/identify/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"1\":\"Product 1\"}"))
                .andExpect(status().isOk());

        verify(service, times(1)).customerIdentify(any());
    }
}
