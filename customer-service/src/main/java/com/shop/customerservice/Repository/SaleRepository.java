package com.shop.customerservice.Repository;

import com.shop.customerservice.Model.Sale;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SaleRepository extends MongoRepository<Sale, Long> {
    List<Sale> findAllByCustomerId(Long customerId);
}

