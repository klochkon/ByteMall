package com.shop.customerservice.repository;

import com.shop.customerservice.model.Order;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, ObjectId> {
    List<Order> findAllByCustomerId(String customerId);
}
