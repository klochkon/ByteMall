package com.shop.customerservice.service;

import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.OrderDuplicateDTO;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.dto.ProductDuplicateDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository repository;
    private final ProductClient productClient;
    private Map<Long, Integer> cartWithId;

    @CachePut(value = {"order", "allOrders"}, key = "#orderDuplicateDTO.id")
    @KafkaListener(topics = "order-topic", groupId = "${spring.kafka.consumer-groups.order-group.group-id}")
    public Order saveOrder(OrderWithProductCartDTO orderDuplicateDTO) {
        log.info("Received order for saving: {}", orderDuplicateDTO);
        cartWithId = new HashMap<>();
        for (Map.Entry<ProductDuplicateDTO, Integer> entry : orderDuplicateDTO.getCart().entrySet()) {
            cartWithId.put(entry.getKey().getId(), entry.getValue());
        }
        Order order = Order.builder()
                .id(new ObjectId(orderDuplicateDTO.getId()))
                .cart(cartWithId)
                .customerId(orderDuplicateDTO.getCustomerId())
                .cost(orderDuplicateDTO.getCost())
                .build();

        Order savedOrder = repository.save(order);
        log.info("Order saved successfully: {}", savedOrder);
        return savedOrder;
    }

    @CachePut(value = {"order", "allOrders"}, key = "#orderDuplicateDTO.id")
    public Order updateOrder(OrderWithProductCartDTO orderDuplicateDTO) {
        log.info("Updating order: {}", orderDuplicateDTO);
        cartWithId = new HashMap<>();
        for (Map.Entry<ProductDuplicateDTO, Integer> entry : orderDuplicateDTO.getCart().entrySet()) {
            cartWithId.put(entry.getKey().getId(), entry.getValue());
        }
        Order order = Order.builder()
                .id(new ObjectId(orderDuplicateDTO.getId()))
                .cart(cartWithId)
                .customerId(orderDuplicateDTO.getCustomerId())
                .cost(orderDuplicateDTO.getCost())
                .build();
        log.info("Order updated successfully: {}", order);
        return order;
    }

    @CacheEvict(value = {"order", "allOrders"}, key = "#id")
    public void deleteOrderById(String id) {
        log.info("Deleting order with id: {}", id);
        repository.deleteById(new ObjectId(id));
        log.info("Order with id {} deleted successfully", id);
    }

    @Cacheable(value = "order", key = "#id")
    public OrderWithProductCartDTO findOrderById(String id) {
        log.info("Finding order by id: {}", id);
        Order order = repository.findById(new ObjectId(id)).orElse(null);
        log.info("Order found: {}", order);

        List<Long> listId = new ArrayList<>();
        List<Integer> listQuantity = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : order.getCart().entrySet()) {
            listId.add(entry.getKey());
            listQuantity.add(entry.getValue());
        }
        List<ProductDuplicateDTO> listProducts = productClient.nameIdentifier(listId);
        Map<ProductDuplicateDTO, Integer> cartWithProduct = new HashMap<>();
        for (ProductDuplicateDTO product : listProducts) {
            cartWithProduct.put(product, listQuantity.remove(0));
        }


        OrderWithProductCartDTO orderDuplicateDTO;
        orderDuplicateDTO = OrderWithProductCartDTO.builder()
                .cost(order.getCost())
                .id(order.getId().toHexString())
                .customerId(order.getCustomerId())
                .cart(cartWithProduct)
                .build();
        return orderDuplicateDTO;
    }

    @Cacheable(value = "allOrders")
    public List<OrderWithProductCartDTO> findAllByCustomerId(String customerId) {
        log.info("Finding all orders for customer id: {}", customerId);
        List<Order> orders = repository.findAllByCustomerId(customerId);
        List<OrderDuplicateDTO> orderDuplicateDTOList = new ArrayList<>();
        for (Order order : orders) {
            OrderDuplicateDTO orderDuplicateDTO;
            orderDuplicateDTO = OrderDuplicateDTO.builder()
                    .customerId(order.getCustomerId())
                    .id(order.getId().toHexString())
                    .cost(order.getCost())
                    .cart(order.getCart())
                    .build();
            orderDuplicateDTOList.add(orderDuplicateDTO);
        }
        log.info("Total orders found for customer id {}: {}", customerId, orders.size());
        return productClient.groupNameIdentifier(orderDuplicateDTOList);
    }
}
