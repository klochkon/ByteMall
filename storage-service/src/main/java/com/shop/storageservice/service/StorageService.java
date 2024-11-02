package com.shop.storageservice.service;

import com.shop.storageservice.client.CustomerClient;
import com.shop.storageservice.client.ProductClient;
import com.shop.storageservice.dto.*;
import com.shop.storageservice.model.Storage;
import com.shop.storageservice.repository.StorageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Service
@RequiredArgsConstructor
@Slf4j
public class StorageService {

    private final KafkaTemplate<String, List<StorageDuplicateDTO>> kafkaProductVerification;

    private final StorageRepository repository;

    private final CustomerClient customerClient;
    private final ProductClient productClient;

    private Map<Long, String> outMapWithId = new HashMap<>();

    @PersistenceContext
    private EntityManager entityManager;

    @CachePut(value = "storage", key = "#productDuplicateDTO.id")
    public void addProductById(ProductDuplicateDTO productDuplicateDTO, Integer quantityAdded) {
        repository.addProductById(productDuplicateDTO.getId(), quantityAdded);
        log.info("Product added: {} with quantity: {}", productDuplicateDTO.getName(), quantityAdded);
        Map<String, String> productsWasOutMap = new HashMap<>();
        for (Map.Entry<Long, String> entry : outMapWithId.entrySet()) {
            if (entry.getKey().equals(productDuplicateDTO.getId())) {
                productsWasOutMap.put(entry.getValue(), productDuplicateDTO.getName());
            }
        }
        if (!productsWasOutMap.isEmpty()) {
            customerClient.customerIdentify(productsWasOutMap);
            log.info("Notified customers for products that were out of stock: {}", productsWasOutMap);
        }
    }

    @CachePut(value = "storage", key = "#productDuplicateDTO.id")
    public void saveProduct(Integer quantity, ProductDuplicateDTO productDuplicateDTO) {
        Storage storage = Storage.builder()
                .productId(productDuplicateDTO.getId())
                .quantity(quantity)
                .build();
        repository.save(storage);
        log.info("Product saved: {} with quantity: {}", productDuplicateDTO.getName(), quantity);
    }

    @CachePut(value = "storage", key = "#productDuplicateDTO.id")
    public void updateProduct(Integer quantity, ProductDuplicateDTO productDuplicateDTO) {
        Storage storage = Storage.builder()
                .productId(productDuplicateDTO.getId())
                .quantity(quantity)
                .build();
        repository.save(storage);
        log.info("Product updated: {} with new quantity: {}", productDuplicateDTO.getName(), quantity);
    }

    public List<ProductWithQuantityDTO> findAllStorageWithQuantity() {
        List<Storage> allProducts = repository.findAll();
        List<StorageDuplicateDTO> storageList = new ArrayList<>();
        for (Storage product : allProducts) {
            StorageDuplicateDTO storageDuplicateDTO = StorageDuplicateDTO.builder()
                    .productId(product.getProductId())
                    .quantity(product.getQuantity())
                    .build();
            storageList.add(storageDuplicateDTO);
        }
        log.info("Retrieved all products with quantities: {}", storageList);
        return productClient.getAllProductWithQuantity(storageList);
    }

    @CacheEvict(value = "storage", key = "#id")
    public void deleteById(Long id) {
        repository.deleteById(id);
        log.info("Deleted product with ID: {}", id);
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void productVerification() {
        List<Storage> allProducts = repository.findAll();
        List<StorageDuplicateDTO> productsWithLack = new ArrayList<>();
        for (Storage product : allProducts) {
            if (product.getQuantity() <= 10) {
                StorageDuplicateDTO storageDuplicateDTO = StorageDuplicateDTO.builder()
                        .productId(product.getProductId())
                        .quantity(product.getQuantity())
                        .build();
                productsWithLack.add(storageDuplicateDTO);
            }
        }
        kafkaProductVerification.send("product-name-identifier-topic", productsWithLack);
        log.info("Sent product verification message for low stock products: {}", productsWithLack);
    }

    @Cacheable(value = "storage", key = "#id")
    public Storage findById(Long id) {
        Storage storage = repository.findById(id).orElse(null);
        log.info("Finding product by ID: {}: {}", id, storage);
        return storage;
    }

    @Cacheable(value = "is-in-storage", key = "#id")
    public Boolean isInStorage(Long id, Integer requiredQuantity) {
        Storage product = repository.findById(id).orElse(null);
        boolean inStorage = product != null && product.getQuantity() >= requiredQuantity;
        log.info("Checking storage for product ID: {} with required quantity {}: {}", id, requiredQuantity, inStorage);
        return inStorage;
    }

    @KafkaListener(topics = "order-topic", groupId = "${spring.kafka.consumer-groups.order-group.group-id}")
    @CacheEvict(value = "storage", key = "#orderDuplicateDTO.id")
    public void reduceQuantityById(OrderWithProductCartDTO orderDuplicateDTO) {
        for (Map.Entry<ProductDuplicateDTO, Integer> entry : orderDuplicateDTO.getCart().entrySet()) {
            entityManager.createNativeQuery("UPDATE storage " +
                            "SET quantity = quantity - :deletedQuantity " +
                            "WHERE id = :id")
                    .setParameter("deletedQuantity", entry.getValue())
                    .setParameter("id", entry.getKey().getId())
                    .executeUpdate();
            entityManager.flush();
            entityManager.clear();
            log.info("Updated storage for product ID: {} by subtracting quantity: {}", entry.getKey().getId(), entry.getValue());
        }
    }

    public Boolean isOrderInStorage(Map<ProductDuplicateDTO, Integer> cart) {
        for (Map.Entry<ProductDuplicateDTO, Integer> entry : cart.entrySet()) {
            if (!this.isInStorage(entry.getKey().getId(), entry.getValue())) {
                log.warn("Product ID: {} is not in storage for quantity: {}", entry.getKey().getId(), entry.getValue());
                return false;
            }
        }
        log.info("All products are in storage for the order.");
        return true;
    }

    public Map<ProductDuplicateDTO, Integer> findOutOfStorageProduct(
            Map<ProductDuplicateDTO, Integer> cart, String customerId) {
        Map<ProductDuplicateDTO, Integer> outOfStorageProduct = new HashMap<>();
        for (Map.Entry<ProductDuplicateDTO, Integer> entry : cart.entrySet()) {
            if (!isInStorage(entry.getKey().getId(), entry.getValue())) {
                outOfStorageProduct.put(entry.getKey(), entry.getValue());
                outMapWithId.put(entry.getKey().getId(), customerId);
                log.warn("Product ID: {} is out of stock, added to outMap for customer ID: {}", entry.getKey().getId(), customerId);
            }
        }
        return outOfStorageProduct;
    }
}
