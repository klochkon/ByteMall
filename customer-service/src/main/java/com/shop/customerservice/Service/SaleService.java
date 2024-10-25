package com.shop.customerservice.Service;

import com.shop.customerservice.DTO.SaleDuplicateDTO;
import com.shop.customerservice.Model.Sale;
import com.shop.customerservice.Repository.SaleRepository;
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
        log.info("Received sale DTO for saving: {}", saleDuplicateDTO);
        Sale sale = Sale.builder()
                .id(saleDuplicateDTO.getId())
                .sale(saleDuplicateDTO.getSale())
                .customerId(saleDuplicateDTO.getCustomerId())
                .build();

        Sale savedSale = repository.save(sale);
        log.info("Sale DTO saved successfully: {}", savedSale);
        return savedSale;
    }

    @CacheEvict(value = {"sale", "allSales"}, key = "#id")
    public void deleteSaleById(Long id) {
        repository.deleteById(id);
        log.info("Sale with id {} deleted successfully", id);
    }

    @Cacheable(value = "sale", key = "#id")
    public Sale findSaleById(Long id) {
        Sale sale = repository.findById(id).orElse(null);
        log.info("Sale found: {}", sale);
        return sale;
    }

    @Cacheable(value = "allOrders")
    public List<Sale> findAllByCustomerId(Long customerId) {
        List<Sale> sales = repository.findAllByCustomerId(customerId);
        log.info("Total sales found for customer id {}: {}", customerId, sales.size());
        return sales;
    }
}
