package com.shop.customerservice.service;

import com.shop.customerservice.dto.SaleDuplicateDTO;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {

    private final SaleRepository repository;

    @CachePut(value = "sale", key = "#sale.id")
    public Sale updateSale(Sale sale) {
        Sale updatedSale = repository.save(sale);
        log.info("Sale updated successfully: {}", updatedSale);
        return updatedSale;
    }

    @CachePut(value = {"sale", "allSales"}, key = "#sale.id")
    public Sale saveSale(Sale sale) {
        Sale savedSale = repository.save(sale);
        log.info("Sale saved successfully: {}", savedSale);
        return savedSale;
    }

    @CachePut(value = {"sale", "allSales"}, key = "#saleDuplicateDTO.id")
    @KafkaListener(topics = "sale-topic", groupId = "${spring.kafka.consumer-groups.sale-group.group-id}")
    public Sale saveSaleDTO(SaleDuplicateDTO saleDuplicateDTO) {
        log.info("Received sale dto for saving: {}", saleDuplicateDTO);
        Sale sale = Sale.builder()
                .id(new ObjectId(saleDuplicateDTO.getId()))
                .sale(saleDuplicateDTO.getSale())
                .customerId(saleDuplicateDTO.getCustomerId())
                .build();

        Sale savedSale = repository.save(sale);
        log.info("Sale dto saved successfully: {}", savedSale);
        return savedSale;
    }

    @CacheEvict(value = {"sale", "allSales"}, key = "#id")
    public void deleteSaleById(String id) {
        repository.deleteById(new ObjectId(id));
        log.info("Sale with id {} deleted successfully", id);
    }

    @Cacheable(value = "sale", key = "#id")
    public Sale findSaleById(String id) {
        Sale sale = repository.findById(new ObjectId(id)).orElse(null);
        log.info("Sale found: {}", sale);
        return sale;
    }

    @Cacheable(value = "allOrders")
    public List<Sale> findAllByCustomerId(String customerId) {
        List<Sale> sales = repository.findAllByCustomerId(new ObjectId(customerId));
        log.info("Total sales found for customer id {}: {}", customerId, sales.size());
        return sales;
    }
}
