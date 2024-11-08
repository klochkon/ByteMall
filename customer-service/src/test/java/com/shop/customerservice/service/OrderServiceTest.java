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
                .cart(Map.of(1L, 1))
                .build();
    }

    @Test
    void saveOrder() {
//        given
        when(repository.save(any(Order.class))).thenReturn(order);

//        when
        Order savedOrder = orderService.saveOrder(orderWithProductCartDTO);

//        then
        verify(repository).save(any(Order.class));
        assertEquals(orderWithProductCartDTO.getCustomerId(), savedOrder.getCustomerId());
        assertEquals(orderWithProductCartDTO.getCost(), savedOrder.getCost());
    }

    @Test
    void updateOrder() {
//        given
        when(repository.save(any(Order.class))).thenReturn(order);

//        when
        Order updatedOrder = orderService.updateOrder(orderWithProductCartDTO);

//        then
        verify(repository, times(1)).save(order);
        assertEquals(order.getCustomerId(), updatedOrder.getCustomerId());
        assertEquals(orderWithProductCartDTO.getCost(), updatedOrder.getCost());
    }

    @Test
    void deleteOrderById() {
//        given
        doNothing().when(repository).deleteById(any(ObjectId.class));

//        when
        orderService.deleteOrderById(order.getId().toHexString());

//        then
        verify(repository).deleteById(new ObjectId(order.getId().toHexString()));
    }

    @Test
    void findOrderById() {
//        given
        when(repository.findById(any(ObjectId.class))).thenReturn(Optional.of(order));
        when(productClient.nameIdentifier(anyList())).thenReturn(List.of(productDuplicateDTO));

//        when
        OrderWithProductCartDTO foundOrder = orderService.findOrderById(order.getId().toHexString());

//        then
        verify(repository).findById(new ObjectId(order.getId().toHexString()));
        assertNotNull(foundOrder);
        assertEquals(order.getCustomerId(), foundOrder.getCustomerId());
    }

    @Test
    void findAllByCustomerId() {
//        given
        when(repository.findAllByCustomerId(anyString())).thenReturn(List.of(order));
        when(productClient.groupNameIdentifier(anyList())).thenReturn(List.of(orderWithProductCartDTO));

//        when
        List<OrderWithProductCartDTO> orders = orderService.findAllByCustomerId(order.getCustomerId());

//        then
        verify(repository).findAllByCustomerId(order.getCustomerId());
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
    }
}
