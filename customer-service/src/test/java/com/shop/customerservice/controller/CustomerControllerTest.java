package com.shop.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.customerservice.dto.CustomerDTO;
import com.shop.customerservice.dto.CustomerWithCartDTO;
import com.shop.customerservice.enums.Gender;
import com.shop.customerservice.model.Customer;
import com.shop.customerservice.service.CustomerService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    private Customer customer;
    private CustomerWithCartDTO customerWithCartDTO;
    private CustomerDTO customerDTO;

    @BeforeEach
    void setUp() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 2);
        customer = Customer.builder()
                .id(new ObjectId())
                .email("test@example.com")
                .phoneNumber("123456789")
                .nickName("testNick")
                .name("Test Name")
                .surname("Test Surname")
                .gender(Gender.MALE)
                .cart(cart)
                .newsLetterSubscribe(true)
                .build();

        customerWithCartDTO = CustomerWithCartDTO.builder()
                .id("id")
                .email("test@example.com")
                .phoneNumber("123456789")
                .nickName("testNick")
                .name("Test Name")
                .surname("Test Surname")
                .newsLetterSubscribe(true)
                .build();

        customerDTO = CustomerDTO.builder()
                .name("Test Customer")
                .email("test@example.com")
                .build();
    }

    @Test
    void testSaveCustomer() throws Exception {
//        given
        when(customerService.saveCustomer(any(Customer.class))).thenReturn(customer);

//        when
        mockMvc.perform(post("/api/v1/customer/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.nickName").value("testNick"))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.surname").value("Test Surname"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.newsLetterSubscribe").value(true));

//        then
        verify(customerService, times(1)).saveCustomer(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() throws Exception {
//        given
        when(customerService.updateCustomer(any(Customer.class))).thenReturn(customer);

//        when
        mockMvc.perform(put("/api/v1/customer/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.phoneNumber").value("123456789"))
                .andExpect(jsonPath("$.nickName").value("testNick"))
                .andExpect(jsonPath("$.name").value("Test Name"))
                .andExpect(jsonPath("$.surname").value("Test Surname"))
                .andExpect(jsonPath("$.gender").value("MALE"))
                .andExpect(jsonPath("$.newsLetterSubscribe").value(true));

//        then
        verify(customerService, times(1)).updateCustomer(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() throws Exception {
//        given
        doNothing().when(customerService).deleteCustomerById(anyString());

//        when
        mockMvc.perform(delete("/api/v1/customer/delete/1"))
                .andExpect(status().isOk());

//        then
        verify(customerService, times(1)).deleteCustomerById("1");
    }

    @Test
    void testFindCustomerById() throws Exception {
//        given
        when(customerService.findCustomerById(anyString())).thenReturn(customerWithCartDTO);

//        when
        mockMvc.perform(get("/api/v1/customer/find/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(customerWithCartDTO)));

//        then
        verify(customerService, times(1)).findCustomerById("1");
    }

    @Test
    void testFindCustomerEmailAndNameById() throws Exception {
//        given
        when(customerService.findCustomerEmailAndNameById(anyString())).thenReturn(customerDTO);

//        when
        mockMvc.perform(get("/api/v1/customer/find/customerDTO/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(customerDTO)));

//        then
        verify(customerService, times(1)).findCustomerEmailAndNameById("1");
    }

    @Test
    void testFindAllCustomer() throws Exception {
//        given
        List<Customer> customers = List.of(customer);
        when(customerService.findAllCustomer()).thenReturn(customers);

//        when
        mockMvc.perform(get("/api/v1/customer/find/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].email").value("test@example.com"))
                .andExpect(jsonPath("$[0].phoneNumber").value("123456789"))
                .andExpect(jsonPath("$[0].nickName").value("testNick"))
                .andExpect(jsonPath("$[0].name").value("Test Name"))
                .andExpect(jsonPath("$[0].surname").value("Test Surname"))
                .andExpect(jsonPath("$[0].gender").value("MALE"))
                .andExpect(jsonPath("$[0].newsLetterSubscribe").value(true));

//        then
        verify(customerService, times(1)).findAllCustomer();
    }

    @Test
    void testCleanCart() throws Exception {
//        given
        doNothing().when(customerService).cleanCart(anyString());

//        when
        mockMvc.perform(put("/api/v1/customer/clean/cart/1"))
                .andExpect(status().isOk());

//        then
        verify(customerService, times(1)).cleanCart("1");
    }

    @Test
    void testCustomerIdentify() throws Exception {
//        given
        doNothing().when(customerService).customerIdentify(any());
        Map<String, String> productsWasOutMap = Map.of("key", "value");

//        when
        mockMvc.perform(put("/api/v1/customer/identify/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productsWasOutMap)))
                .andExpect(status().isOk());

//        then
        verify(customerService, times(1)).customerIdentify(any(Map.class));
    }
}
