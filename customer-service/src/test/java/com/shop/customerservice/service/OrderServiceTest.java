package com.shop.customerservice.service;

import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.repository.OrderRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

        orderDto = OrderWithProductCartDTO.builder()
                .id("id")
                .customerId("1L")
                .cost(new BigDecimal("100.0"))
                .cart(new HashMap<>())
                .build();

        order = Order.builder()
                .id(new ObjectId("id"))
                .customerId("1L")
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
        doNothing().when(orderRepository).deleteById(any());

        orderService.deleteOrderById(order.getId().toHexString());

        verify(orderRepository, times(1)).deleteById(order.getId());
    }

    @Test
    public void testFindOrderById() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        OrderWithProductCartDTO foundOrder = orderService.findOrderById(orderDto.getId());

        assertNotNull(foundOrder);
        assertEquals(1L, foundOrder.getId());
        verify(orderRepository, times(1)).findById(any());
    }

    @Test
    public void testFindAllByCustomerId() {
        when(orderRepository.findAllByCustomerId(anyString())).thenReturn(List.of(order));
        when(productClient.groupNameIdentifier(any())).thenReturn(List.of());

        List<OrderWithProductCartDTO> orders = orderService.findAllByCustomerId(orderDto.getCustomerId());

        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(orderRepository, times(1)).findAllByCustomerId(orderDto.getCustomerId());
    }
}
