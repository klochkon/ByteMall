package com.shop.customerservice.repository;

import com.shop.customerservice.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, Long> {
    List<Order> findAllByCustomerId(Long customerId);
}
