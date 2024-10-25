package com.shop.storageservice.Service;



import com.shop.storageservice.Client.CustomerClient;
import com.shop.storageservice.Client.ProductClient;
import com.shop.storageservice.DTO.OrderDuplicateDTO;
import com.shop.storageservice.DTO.ProductDuplicateDTO;
import com.shop.storageservice.DTO.ProductWithQuantityDTO;
import com.shop.storageservice.DTO.StorageDuplicateDTO;
import com.shop.storageservice.Model.Storage;
import com.shop.storageservice.Repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class StorageServiceTest {

    @Mock
    private KafkaTemplate<String, List<StorageDuplicateDTO>> kafkaProductVerification;

    @Mock
    private StorageRepository repository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private StorageService service;

    private ProductDuplicateDTO productDuplicateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productDuplicateDTO = ProductDuplicateDTO.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .cost(new BigDecimal("9.99"))
                .producer("Test Producer")
                .category("Test Category")
                .feedBack(new BigDecimal("4.5"))
                .build();
    }

    @Test
    void addProductById() {
        service.addProductById(productDuplicateDTO, 10);

        verify(repository, times(1)).addProductById(productDuplicateDTO.getId(), 10);
        verify(customerClient, times(1)).customerIdentify(any());
    }

    @Test
    void saveProduct() {
        service.saveProduct(100, productDuplicateDTO);

        verify(repository, times(1)).save(any(Storage.class));
    }

    @Test
    void updateProduct() {
        service.updateProduct(200, productDuplicateDTO);

        verify(repository, times(1)).save(any(Storage.class));
    }

    @Test
    void findAllStorageWithQuantity() {
        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(100)
                .build();

        when(repository.findAll()).thenReturn(Collections.singletonList(storage));
        when(productClient.getAllProductWithQuantity(anyList())).thenReturn(new ArrayList<>());

        List<ProductWithQuantityDTO> result = service.findAllStorageWithQuantity();

        assertEquals(0, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deleteById() {
        service.deleteById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void findById() {
        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(100)
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

        Storage result = service.findById(1L);

        assertEquals(storage, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isInStorage() {
        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(100)
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

        Boolean result = service.isInStorage(1L, 50);

        assertTrue(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isOrderInStorage() {
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 5);

        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(10)
                .build();

        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

        Boolean result = service.isOrderInStorage(cart);

        assertTrue(result);
    }

    @Test
    void findOutOfStorageProduct() {
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 15);

        Map<ProductDuplicateDTO, Integer> result = service.findOutOfStorageProduct(cart, 1L);

        assertTrue(result.containsKey(productDuplicateDTO));
    }

    @Test
    void productVerification() {
        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(5)
                .build();

        when(repository.findAll()).thenReturn(Collections.singletonList(storage));

        service.productVerification();

        verify(kafkaProductVerification, times(1)).send(eq("product-name-identifier-topic"), anyList());
    }

    @Test
    void deleteProductById() {
        OrderDuplicateDTO orderDuplicateDTO = new OrderDuplicateDTO();
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 3);
        orderDuplicateDTO.setCart(cart);

        Storage storage = Storage.builder()
                .productId(1L)
                .quantity(100)
                .build();

        when(repository.findById(productDuplicateDTO.getId())).thenReturn(Optional.of(storage));

        service.deleteProductById(orderDuplicateDTO);

        verify(repository, times(1)).deleteById(productDuplicateDTO.getId());
    }
}
