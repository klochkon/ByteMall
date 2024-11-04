package com.shop.customerservice.service;

import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.dto.ProductDuplicateDTO;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository repository;

    @Mock
    private ProductClient productClient;

    private OrderWithProductCartDTO orderWithProductCartDTO;
    private Order order;
    private ProductDuplicateDTO productDuplicateDTO;

    @BeforeEach
    void setUp() {
        productDuplicateDTO = ProductDuplicateDTO.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .cost(new BigDecimal("9.99"))
                .producer("Test Producer")
                .category("Test Category")
                .feedBack(new BigDecimal("4.5"))
                .build();

        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 1);
        orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                .id(new ObjectId().toHexString())
                .customerId("customerId")
                .cost(BigDecimal.valueOf(100))
                .cart(cart)
                .build();

        order = Order.builder()
                .id(new ObjectId(orderWithProductCartDTO.getId()))
                .customerId(orderWithProductCartDTO.getCustomerId())
                .cost(orderWithProductCartDTO.getCost())
                .cart(Map.of(1L, 2))
                .build();
    }

    @Test
    void saveOrder() {
        when(repository.save(any(Order.class))).thenReturn(order);

        Order savedOrder = orderService.saveOrder(orderWithProductCartDTO);

        verify(repository).save(any(Order.class));
        assertEquals(orderWithProductCartDTO.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(orderWithProductCartDTO.getCost(), savedOrder.getCost());
    }

    @Test
    void updateOrder() {
        when(repository.save(any(Order.class))).thenReturn(order);

        Order updatedOrder = orderService.updateOrder(order);

        verify(repository).save(order);
        assertEquals(order.getCustomerId(), updatedOrder.getCustomerId());
    }

    @Test
    void deleteOrderById() {
        doNothing().when(repository).deleteById(any(ObjectId.class));

        orderService.deleteOrderById(order.getId().toHexString());

        verify(repository).deleteById(new ObjectId(order.getId().toHexString()));
    }

    @Test
    void findOrderById() {
        when(repository.findById(any(ObjectId.class))).thenReturn(Optional.of(order));
        when(productClient.nameIdentifier(anyList())).thenReturn(List.of(productDuplicateDTO));

        OrderWithProductCartDTO foundOrder = orderService.findOrderById(order.getId().toHexString());

        verify(repository).findById(new ObjectId(order.getId().toHexString()));
        assertNotNull(foundOrder);
        assertEquals(order.getCustomerId(), foundOrder.getCustomerId());
    }

    @Test
    void findAllByCustomerId() {
        when(repository.findAllByCustomerId(anyString())).thenReturn(List.of(order));
        when(productClient.groupNameIdentifier(anyList())).thenReturn(List.of(orderWithProductCartDTO));

        List<OrderWithProductCartDTO> orders = orderService.findAllByCustomerId(order.getCustomerId());

        verify(repository).findAllByCustomerId(order.getCustomerId());
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
}
