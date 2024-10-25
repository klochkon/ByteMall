package com.shop.customerservice.Service;

import com.shop.customerservice.DTO.OrderDuplicateDTO;
import com.shop.customerservice.Model.Order;
import com.shop.customerservice.Repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository repository;

    @KafkaListener(topics = "order-topic", groupId = "${spring.kafka.consumer-groups.order-group.group-id}")
    public Order saveOrder(OrderDuplicateDTO orderDuplicateDTO) {
        log.info("Received order for saving: {}", orderDuplicateDTO);
        Order order = Order.builder()
                .id(orderDuplicateDTO.getId())
                .cart(orderDuplicateDTO.getCart())
                .customerId(orderDuplicateDTO.getCustomerId())
                .cost(orderDuplicateDTO.getCost())
                .build();

        Order savedOrder = repository.save(order);
        log.info("Order saved successfully: {}", savedOrder);
        return savedOrder;
    }

    @CachePut(value = {"order", "allOrders"}, key = "#order.id")
    public Order updateOrder(Order order) {
        log.info("Updating order: {}", order);
        Order updatedOrder = repository.save(order);
        log.info("Order updated successfully: {}", updatedOrder);
        return updatedOrder;
    }

    @CacheEvict(value = {"order", "allOrders"}, key = "#id")
    public void deleteOrderById(Long id) {
        log.info("Deleting order with id: {}", id);
        repository.deleteById(id);
        log.info("Order with id {} deleted successfully", id);
    }

    @Cacheable(value = "order", key = "#id")
    public Order findOrderById(Long id) {
        log.info("Finding order by id: {}", id);
        Order order = repository.findById(id).orElse(null);
        log.info("Order found: {}", order);
        return order;
    }

    @Cacheable(value = "allOrders")
    public List<Order> findAllByCustomerId(Long customerId) {
        log.info("Finding all orders for customer id: {}", customerId);
        List<Order> orders = repository.findAllByCustomerId(customerId);
        log.info("Total orders found for customer id {}: {}", customerId, orders.size());
        return orders;
    }
}
