package com.shop.customerservice.Service;

import com.shop.customerservice.DTO.OrderWithProductCartDTO;
import com.shop.customerservice.Model.Order;
import com.shop.customerservice.Repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository repository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderWithProductCartDTO orderDuplicateDTO;

    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(1L)
                .customerId(1L)
                .cart(Map.of())
                .cost(BigDecimal.valueOf(100.0))
                .build();

        orderDuplicateDTO = OrderWithProductCartDTO.builder()
                .id(1L)
                .customerId(1L)
                .cart(Map.of())
                .cost(BigDecimal.valueOf(100.0))
                .build();
    }

    @Test
    void saveOrder() {
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.saveOrder(orderDuplicateDTO);

        assertEquals(order, result);
        verify(repository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrder() {
        when(repository.save(any(Order.class))).thenReturn(order);

        Order result = orderService.updateOrder(order);

        assertEquals(order, result);
        verify(repository, times(1)).save(order);
    }

    @Test
    void deleteOrderById() {
        doNothing().when(repository).deleteById(anyLong());

        orderService.deleteOrderById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void findOrderById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(1L);

        assertEquals(order, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findOrderById_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Order result = orderService.findOrderById(1L);

        assertNull(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findAllByCustomerId() {
        List<Order> orders = List.of(order);
        when(repository.findAllByCustomerId(anyLong())).thenReturn(orders);

        List<Order> result = orderService.findAllByCustomerId(1L);

        assertEquals(orders, result);
        verify(repository, times(1)).findAllByCustomerId(1L);
    }
}
