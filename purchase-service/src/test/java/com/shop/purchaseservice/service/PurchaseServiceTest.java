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
import java.util.concurrent.CompletableFuture;

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

    private SaleDuplicateDTO sale;

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
                .cost(new BigDecimal(600))
                .build();

        sale = SaleDuplicateDTO.builder()
                .id("id")
                .customerId("customerId")
                .sale(new BigDecimal(700))
                .build();
    }

    @Test
    void testPurchase_OrderInStorage() {
        when(storageClient.isOrderInStorage(any())).thenReturn(true);
        doNothing().when(purchaseService).purchaseLogicIfOrderInStorage(orderWithProductCartDTO);

        InventoryStatusDTO result = purchaseService.purchase(orderWithProductCartDTO);

        verify(purchaseService,times(1)).purchaseLogicIfOrderInStorage(orderWithProductCartDTO);
        assertTrue(result.getIsOrderInStorage());
        verify(storageClient, times(1)).isOrderInStorage(any());
    }

    @Test
    void testPurchase_OrderNotInStorage() {
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

        InventoryStatusDTO result = purchaseService.purchase(orderWithProductCartDTO);

        assertFalse(result.getIsOrderInStorage());
        verify(storageClient, times(1)).findOutOfStorageProduct(cart, orderWithProductCartDTO.getCustomerId());
    }

    @Test
    void testPurchaseLogicIfOrderInStorage() {
        doNothing().when(customerClient).cleanCart(anyString());
        when(kafkaSale.send(eq("sale-topic"), eq(sale)))
                .thenReturn(CompletableFuture.completedFuture(null));

        doNothing().when(purchaseService).purchaseMailSend(eq(orderWithProductCartDTO));

        when(kafkaAddOrder.send(eq("order-topic"), eq(orderWithProductCartDTO)))
                .thenReturn(CompletableFuture.completedFuture(null));

        purchaseService.purchaseLogicIfOrderInStorage(orderWithProductCartDTO);

        verify(kafkaAddOrder, times(1)).send(eq("order-topic"), eq(orderWithProductCartDTO));
        verify(customerClient, times(1)).cleanCart(orderWithProductCartDTO.getCustomerId());
        verify(kafkaSale, times(1)).send(eq("sale-topic"), eq(sale));
    }

    @Test
    void testPurchaseLogicIfOrderNotInStorage() {
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

        Map<ProductDuplicateDTO, Integer> result = purchaseService.purchaseLogicIfOrderNotInStorage(orderWithProductCartDTO);

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
        CustomerDTO customerDTO = CustomerDTO.builder()
                .email("test@example.com")
                .name("Test Customer")
                .build();
        MailDTO mailDTO = MailDTO.builder()
                .to("email")
                .data(Map.of("string", "object"))
                .build();
        when(customerClient.findCustomerEmailAndNameById(anyString())).thenReturn(customerDTO);
        when(kafkaMail.send(eq("mail-topic"), eq(mailDTO)))
                .thenReturn(CompletableFuture.completedFuture(null));

        purchaseService.purchaseMailSend(orderWithProductCartDTO);

        verify(kafkaMail, times(1)).send(eq("mail-topic"), eq(mailDTO));
    }
}
