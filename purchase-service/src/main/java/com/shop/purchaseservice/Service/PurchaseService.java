package com.shop.purchaseservice.Service;

import com.shop.purchaseservice.Client.CustomerClient;
import com.shop.purchaseservice.Client.StorageClient;
import com.shop.purchaseservice.DTO.*;
import lombok.RequiredArgsConstructor;
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
    private final KafkaTemplate<String, OrderDuplicateDTO> kafkaAddOrder;
    private final KafkaTemplate<String, MailDTO> kafkaMail;
    private final CustomerClient customerClient;
    private final KafkaTemplate<String, SaleDuplicateDTO> kafkaSale;

    @Transactional
    public InventoryStatusDTO purchase(OrderDuplicateDTO orderDuplicateDTO) {
        log.info("Processing purchase for order: {}", orderDuplicateDTO);
        InventoryStatusDTO inventoryStatusDTO = new InventoryStatusDTO();

        if (storageClient.isOrderInStorage(orderDuplicateDTO.getCart())) {
            log.info("Order is in storage, sending to Kafka topic.");
            kafkaAddOrder.send("order-topic", orderDuplicateDTO);
            purchaseMailSend(orderDuplicateDTO);
            customerClient.cleanCart(orderDuplicateDTO.getCustomerId());

            if (orderDuplicateDTO.getCost().compareTo(new BigDecimal("500.0")) > 0) {
                SaleDuplicateDTO saleDuplicateDTO = SaleDuplicateDTO.builder()
                        .sale(new BigDecimal("0.05"))
                        .customerId(orderDuplicateDTO.getCustomerId())
                        .build();
                kafkaSale.send("sale-topic", saleDuplicateDTO);
                log.info("Sale sent to Kafka for customerId {}: {}", orderDuplicateDTO.getCustomerId(), saleDuplicateDTO);
            }
            inventoryStatusDTO.setIsOrderInStorage(true);
            log.info("Inventory status updated: {}", inventoryStatusDTO);

        } else {
            log.warn("Order is not in storage, finding out of stock products.");
            Map<ProductDuplicateDTO, Integer> outOfStorage = storageClient.findOutOfStorageProduct(
                    orderDuplicateDTO.getCart(), orderDuplicateDTO.getCustomerId());
            inventoryStatusDTO.setOutOfStorageProducts(outOfStorage);
            log.info("Out of storage products found: {}", outOfStorage);
        }
        return inventoryStatusDTO;
    }

    public void purchaseMailSend(OrderDuplicateDTO orderDuplicateDTO) {
        log.info("Sending purchase email for order ID: {}", orderDuplicateDTO.getId());
        Long customerId = orderDuplicateDTO.getCustomerId();
        CustomerDTO customerDTO = customerClient.findCustomerEmailAndNameById(customerId);
        List<String> listOfProducts = new ArrayList<>();

        Map<String, Object> data = Map.of(
                "Cost", orderDuplicateDTO.getCost(),
                "ID", orderDuplicateDTO.getId(),
                "Products", listOfProducts,
                "Name", customerDTO.getName()
        );

        Map<ProductDuplicateDTO, Integer> cart = orderDuplicateDTO.getCart();

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
