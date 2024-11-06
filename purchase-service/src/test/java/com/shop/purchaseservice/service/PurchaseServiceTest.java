package com.shop.purchaseservice.service;

import com.shop.purchaseservice.client.CustomerClient;
import com.shop.purchaseservice.client.StorageClient;
import com.shop.purchaseservice.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private StorageClient storageClient;
    @Mock
    private KafkaTemplate<String, OrderWithProductCartDTO> kafkaAddOrder;
    @Mock
    private KafkaTemplate<String, MailDTO> kafkaMail;
    @Mock
    private CustomerClient customerClient;
    @Mock
    private KafkaTemplate<String, SaleDuplicateDTO> kafkaSale;


    @Spy
    @InjectMocks
    private PurchaseService purchaseService;

    private OrderWithProductCartDTO orderWithProductCartDTO;
    private Map<ProductDuplicateDTO, Integer> cart;

    @BeforeEach
    void setUp() {

        ProductDuplicateDTO product = ProductDuplicateDTO.builder()
                .id(1L)
                .description("Test product description")
                .cost(BigDecimal.valueOf(100))
                .name("Product1")
                .producer("TestProducer")
                .category("Electronics")
                .feedBack(BigDecimal.valueOf(4.5))
                .build();

        cart = new HashMap<>();
        cart.put(product, 1);

        orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                .id("1L")
                .customerId("1L")
                .cart(cart)
                .cost(new BigDecimal("1000.0"))
                .build();
    }

    @Test
    void testPurchase_OrderInStorage() {
//        given
        when(storageClient.isOrderInStorage(any())).thenReturn(true);
        doNothing().when(purchaseService).purchaseLogicIfOrderInStorage(orderWithProductCartDTO);

//        when
        InventoryStatusDTO result = purchaseService.purchase(orderWithProductCartDTO);

//        then
        verify(purchaseService,times(1)).purchaseLogicIfOrderInStorage(orderWithProductCartDTO);
        assertTrue(result.getIsOrderInStorage());
        verify(storageClient, times(1)).isOrderInStorage(any());
    }

    @Test
    void testPurchase_OrderNotInStorage() {
//        given
        when(storageClient.isOrderInStorage(any())).thenReturn(false);
        Map<ProductDuplicateDTO, Integer> outOfStockProducts = Map.of(
                ProductDuplicateDTO.builder()
                        .id(2L)
                        .description("Out of stock product")
                        .cost(BigDecimal.valueOf(150))
                        .name("Product2")
                        .producer("AnotherProducer")
                        .category("Home Appliances")
                        .feedBack(BigDecimal.valueOf(4.2))
                        .build(), 2
        );
        when(storageClient.findOutOfStorageProduct(cart, orderWithProductCartDTO.getCustomerId())).thenReturn(outOfStockProducts);

//        when
        InventoryStatusDTO result = purchaseService.purchase(orderWithProductCartDTO);

//        then
        assertFalse(result.getIsOrderInStorage());
        verify(storageClient, times(1)).findOutOfStorageProduct(cart, orderWithProductCartDTO.getCustomerId());
    }

    @Test
    void testPurchaseLogicIfOrderInStorage() {
//        given
        doNothing().when(customerClient).cleanCart(anyString());

        doNothing().when(purchaseService).purchaseMailSend(any());

//        when
        purchaseService.purchaseLogicIfOrderInStorage(orderWithProductCartDTO);

//        then
        verify(kafkaAddOrder, times(1)).send(anyString(), any());
        verify(customerClient, times(1)).cleanCart(anyString());
        verify(kafkaSale, times(1)).send(anyString(), any());
    }

    @Test
    void testPurchaseLogicIfOrderNotInStorage() {
//        given
        Map<ProductDuplicateDTO, Integer> outOfStockProducts = Map.of(
                ProductDuplicateDTO.builder()
                        .id(2L)
                        .description("Out of stock product")
                        .cost(BigDecimal.valueOf(150))
                        .name("Product2")
                        .producer("AnotherProducer")
                        .category("Home Appliances")
                        .feedBack(BigDecimal.valueOf(4.2))
                        .build(), 2
        );
        when(storageClient.findOutOfStorageProduct(cart, orderWithProductCartDTO.getCustomerId())).thenReturn(outOfStockProducts);

//        when
        Map<ProductDuplicateDTO, Integer> result = purchaseService.purchaseLogicIfOrderNotInStorage(orderWithProductCartDTO);

//        then
        verify(storageClient, times(1)).findOutOfStorageProduct(cart, orderWithProductCartDTO.getCustomerId());
        assertTrue(result.containsKey(ProductDuplicateDTO.builder()
                .id(2L)
                .description("Out of stock product")
                .cost(BigDecimal.valueOf(150))
                .name("Product2")
                .producer("AnotherProducer")
                .category("Home Appliances")
                .feedBack(BigDecimal.valueOf(4.2))
                .build()));
    }

    @Test
    void testPurchaseMailSend() {
//        given
        CustomerDTO customerDTO = CustomerDTO.builder()
                .email("test@example.com")
                .name("Test Customer")
                .build();
        when(customerClient.findCustomerEmailAndNameById(anyString())).thenReturn(customerDTO);

//        when
        purchaseService.purchaseMailSend(orderWithProductCartDTO);

//        then
        verify(kafkaMail, times(1)).send(eq("mail-topic"), any(MailDTO.class));
    }
}
