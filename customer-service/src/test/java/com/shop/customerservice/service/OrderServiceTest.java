package com.shop.customerservice.service;

import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    private OrderWithProductCartDTO orderDto;
    private Order order;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        orderDto = OrderWithProductCartDTO.builder()
                .id(1L)
                .customerId(1L)
                .cost(new BigDecimal("100.0"))
                .cart(new HashMap<>())
                .build();
//todo tests but controll making objects, give entities to chat
        order = Order.builder()
                .id(1L)
                .customerId(1L)
                .cost(new BigDecimal("100.0"))
                .cart(new HashMap<>())
                .build();
    }

    @Test
    public void testSaveOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.saveOrder(orderDto);

        assertNotNull(savedOrder);
        assertEquals(1L, savedOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testUpdateOrder() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(order);

        assertNotNull(updatedOrder);
        assertEquals(1L, updatedOrder.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    public void testDeleteOrderById() {
        Long orderId = 1L;

        doNothing().when(orderRepository).deleteById(anyLong());

        orderService.deleteOrderById(orderId);

        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void testFindOrderById() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        OrderWithProductCartDTO foundOrder = orderService.findOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindAllByCustomerId() {
        Long customerId = 1L;

        when(orderRepository.findAllByCustomerId(customerId)).thenReturn(List.of(order));
        when(productClient.groupNameIdentifier(any())).thenReturn(List.of());

        List<OrderWithProductCartDTO> orders = orderService.findAllByCustomerId(customerId);

        assertNotNull(orders);
        assertEquals(0, orders.size());
        verify(orderRepository, times(1)).findAllByCustomerId(customerId);
    }
}
