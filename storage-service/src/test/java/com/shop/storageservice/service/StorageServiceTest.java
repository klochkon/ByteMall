package com.shop.storageservice.service;

import com.shop.storageservice.client.CustomerClient;
import com.shop.storageservice.client.ProductClient;
import com.shop.storageservice.dto.OrderWithProductCartDTO;
import com.shop.storageservice.dto.ProductDuplicateDTO;
import com.shop.storageservice.dto.ProductWithQuantityDTO;
import com.shop.storageservice.dto.StorageDuplicateDTO;
import com.shop.storageservice.model.Storage;
import com.shop.storageservice.repository.StorageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private KafkaTemplate<String, List<StorageDuplicateDTO>> kafkaProductVerification;

    @Mock
    private StorageRepository repository;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private StorageService service;

    private ProductDuplicateDTO productDuplicateDTO;

    private Storage storage;

    @BeforeEach
    void setUp() {
        productDuplicateDTO = ProductDuplicateDTO.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .cost(new BigDecimal("9.99"))
                .producer("Test Producer")
                .category("Test Category")
                .feedBack(new BigDecimal("4.5"))
                .build();



        storage = Storage.builder()
                .productId(1L)
                .quantity(10)
                .build();
    }

    @Test
    void addProductById() {
        int quantityAdded = 5;
        service.getOutMapWithId().put(1L, 123L);

        service.addProductById(productDuplicateDTO, quantityAdded);

        verify(repository).addProductById(productDuplicateDTO.getId(), quantityAdded);

        Map<Long, String> expectedProductWasOutMap = new HashMap<>();
        expectedProductWasOutMap.put(123L, "Test Product");
        verify(customerClient).customerIdentify(expectedProductWasOutMap);
        verifyNoMoreInteractions(repository, customerClient);
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
        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

        Storage result = service.findById(1L);

        assertEquals(storage, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isInStorage() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

        Boolean result = service.isInStorage(1L, 50);

        assertFalse(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isOrderInStorage() {
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 5);


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


        when(repository.findAll()).thenReturn(Collections.singletonList(storage));

        service.productVerification();

        verify(kafkaProductVerification, times(1)).send(eq("product-name-identifier-topic"), anyList());
    }

    @Test
    void reduceQuantityById() {
        OrderWithProductCartDTO orderDuplicateDTO = new OrderWithProductCartDTO();
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 1);
        ProductDuplicateDTO productDuplicateDTO2 = ProductDuplicateDTO.builder()
                .id(2L)
                .name("Test Product2")
                .description("Test Description2")
                .cost(new BigDecimal("9.99"))
                .producer("Test Producer2")
                .category("Test Category2")
                .feedBack(new BigDecimal("4.5"))
                .build();
        cart.put(productDuplicateDTO2, 2);
        orderDuplicateDTO.setCart(cart);


        Query queryMock = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(queryMock);
        when(queryMock.executeUpdate()).thenReturn(1);

        service.reduceQuantityById(orderDuplicateDTO);

        verify(entityManager, times(2)).createNativeQuery(anyString());
        verify(queryMock, times(2)).executeUpdate();
    }
}
