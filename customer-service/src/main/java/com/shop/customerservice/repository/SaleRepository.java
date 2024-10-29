package com.shop.customerservice.repository;

import com.shop.customerservice.model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SaleRepository extends MongoRepository<Sale, Long> {
    List<Sale> findAllByCustomerId(Long customerId);
}

