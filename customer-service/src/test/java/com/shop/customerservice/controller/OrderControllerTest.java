package com.shop.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.service.OrderService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order order;
    private OrderWithProductCartDTO orderWithProductCartDTO;

    @BeforeEach
    void setUp() {
        Map<Long, Integer> cart = new HashMap<>();
        cart.put(1L, 1);
        cart.put(2L, 2);
        order = Order.builder()
                .id(new ObjectId())
                .customerId("id")
                .cost(new BigDecimal(1000))
                .cart(cart)
                .build();

        orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                .id("id")
                .customerId("customerId")
                .cost(new BigDecimal(600))
                .build();



    }

    @Test
    void testSaveOrder() throws Exception {
        when(orderService.saveOrder(any(OrderWithProductCartDTO.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/order/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderWithProductCartDTO)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(order)));

        verify(orderService, times(1)).saveOrder(any(OrderWithProductCartDTO.class));
    }

    @Test
    void testUpdateOrder() throws Exception {
        when(orderService.updateOrder(any(Order.class))).thenReturn(order);

        mockMvc.perform(put("/api/v1/order/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(order)));

        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    @Test
    void testDeleteOrderById() throws Exception {
        doNothing().when(orderService).deleteOrderById(anyString());

        mockMvc.perform(delete("/api/v1/order/delete/1"))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrderById("1");
    }

    @Test
    void testFindById() throws Exception {
        when(orderService.findOrderById(anyString())).thenReturn(orderWithProductCartDTO);

        mockMvc.perform(get("/api/v1/order/find/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orderWithProductCartDTO)));

        verify(orderService, times(1)).findOrderById("1");
    }

    @Test
    void testFindByCustomerId() throws Exception {
        List<OrderWithProductCartDTO> orders = List.of(orderWithProductCartDTO);
        when(orderService.findAllByCustomerId(anyString())).thenReturn(orders);

        mockMvc.perform(get("/api/v1/order/find/customerId"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(orders)));

        verify(orderService, times(1)).findAllByCustomerId("customerId");
    }
}
