package com.shop.purchaseservice.service;

import com.shop.purchaseservice.client.CustomerClient;
import com.shop.purchaseservice.client.StorageClient;
import com.shop.purchaseservice.dto.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseService {

    private final StorageClient storageClient;
    private final KafkaTemplate<String, OrderWithProductCartDTO> kafkaAddOrder;
    private final KafkaTemplate<String, MailDTO> kafkaMail;
    private final CustomerClient customerClient;
    private final KafkaTemplate<String, SaleDuplicateDTO> kafkaSale;

    @Transactional
    public InventoryStatusDTO purchase(OrderWithProductCartDTO orderWithProductCartDTO) {
        log.info("Processing purchase for order: {}", orderWithProductCartDTO);
        InventoryStatusDTO inventoryStatusDTO = new InventoryStatusDTO();

        if (storageClient.isOrderInStorage(orderWithProductCartDTO.getCart())) {
            log.info("Order is in storage, sending to Kafka topic.");
            kafkaAddOrder.send("order-topic", orderWithProductCartDTO);
            purchaseMailSend(orderWithProductCartDTO);
            customerClient.cleanCart(orderWithProductCartDTO.getCustomerId());

            if (orderWithProductCartDTO.getCost().compareTo(new BigDecimal("500.0")) > 0) {
                SaleDuplicateDTO saleDuplicateDTO = SaleDuplicateDTO.builder()
                        .sale(new BigDecimal("0.05"))
                        .customerId(orderWithProductCartDTO.getCustomerId())
                        .build();
                kafkaSale.send("sale-topic", saleDuplicateDTO);
                log.info("Sale sent to Kafka for customerId {}: {}", orderWithProductCartDTO.getCustomerId(), saleDuplicateDTO);
            }
            inventoryStatusDTO.setIsOrderInStorage(true);
            log.info("Inventory status updated: {}", inventoryStatusDTO);

        } else {
            log.warn("Order is not in storage, finding out of stock products.");
            Map<ProductDuplicateDTO, Integer> outOfStorage = storageClient.findOutOfStorageProduct(
                    orderWithProductCartDTO.getCart(), orderWithProductCartDTO.getCustomerId());
            inventoryStatusDTO.setIsOrderInStorage(false);
            inventoryStatusDTO.setOutOfStorageProducts(outOfStorage);
            log.info("Out of storage products found: {}", outOfStorage);
        }
        return inventoryStatusDTO;
    }

    public void purchaseMailSend(OrderWithProductCartDTO orderWithProductCartDTO) {
        log.info("Sending purchase email for order ID: {}", orderWithProductCartDTO.getId());
        Long customerId = orderWithProductCartDTO.getCustomerId();
        CustomerDTO customerDTO = customerClient.findCustomerEmailAndNameById(customerId);
        List<String> listOfProducts = new ArrayList<>();

        Map<String, Object> data = Map.of(
                "Cost", orderWithProductCartDTO.getCost(),
                "ID", orderWithProductCartDTO.getId(),
                "Products", listOfProducts,
                "Name", customerDTO.getName()
        );

        Map<ProductDuplicateDTO, Integer> cart = orderWithProductCartDTO.getCart();

        for (Map.Entry<ProductDuplicateDTO, Integer> entry : cart.entrySet()) {
            listOfProducts.add(entry.getKey().getName());
        }

        MailDTO mailDTO = MailDTO.builder()
                .to(customerDTO.getEmail())
                .data(data)
                .build();

        kafkaMail.send("mail-topic", mailDTO);
        log.info("Mail sent to: {} with data: {}", customerDTO.getEmail(), data);
    }
}
