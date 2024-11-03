package com.shop.customerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    private OrderWithProductCartDTO orderDTO;
    private Order savedOrder;
    private Long orderId;
    private Long customerId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        orderId = 1L;
        customerId = 1L;
        orderDTO = new OrderWithProductCartDTO(orderId, customerId, Map.of(), BigDecimal.valueOf(100));
        savedOrder = new Order(orderId, customerId, Map.of(), BigDecimal.valueOf(100));
    }

    @Test
    void saveOrder_ShouldReturnSavedOrder() throws Exception {
        when(orderService.saveOrder(any(OrderWithProductCartDTO.class))).thenReturn(savedOrder);

        mockMvc.perform(post("/api/v1/order/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()));
    }

    @Test
    void updateOrder_ShouldReturnUpdatedOrder() throws Exception {
        when(orderService.updateOrder(any(Order.class))).thenReturn(savedOrder);

        mockMvc.perform(put("/api/v1/order/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(savedOrder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId()));
    }

    @Test
    void deleteOrderById_ShouldCallServiceMethod() throws Exception {
        mockMvc.perform(delete("/api/v1/order/delete/{id}", orderId))
                .andExpect(status().isOk());

        verify(orderService, times(1)).deleteOrderById(orderId);
    }

    @Test
    void findById_ShouldReturnOrder() throws Exception {
        when(orderService.findOrderById(orderId)).thenReturn(orderDTO);

        mockMvc.perform(get("/api/v1/order/find/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));
    }

    @Test
    void findByCustomerId_ShouldReturnListOfOrders() throws Exception {
        when(orderService.findAllByCustomerId(customerId)).thenReturn(List.of(orderDTO));

        mockMvc.perform(get("/api/v1/order/find/{customerId}", customerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderDTO.getId()));
    }
}
