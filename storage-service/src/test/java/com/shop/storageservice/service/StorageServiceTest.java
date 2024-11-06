package com.shop.storageservice.service;

import com.shop.storageservice.client.CustomerClient;
import com.shop.storageservice.client.ProductClient;
import com.shop.storageservice.dto.*;
import com.shop.storageservice.model.Storage;
import com.shop.storageservice.repository.StorageRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    void raiseProductQuantityById() {
//        given
        int quantityAdded = 5;
        doNothing().when(customerClient).customerIdentify(any());
        doNothing().when(repository).raiseProductQuantityById(anyLong(), anyInt());
        Map<Long, String> outMapWithId = new HashMap<>();
        outMapWithId.put(1L, "123");
        service.setOutMapWithId(outMapWithId);
        Map<String, String> expectedProductWasOutMap = new HashMap<>();
        expectedProductWasOutMap.put("123", "Test Product");

//        when
        service.raiseProductQuantityById(productDuplicateDTO, quantityAdded);

//        then
        verify(repository).raiseProductQuantityById(productDuplicateDTO.getId(), quantityAdded);
        verify(customerClient).customerIdentify(expectedProductWasOutMap);
        verifyNoMoreInteractions(repository, customerClient);

    }

    @Test
    void saveProduct() {
//        given
        when(repository.save(any())).thenReturn(storage);

//        when
        service.saveProduct(100, productDuplicateDTO);

//        then
        verify(repository, times(1)).save(any(Storage.class));
    }

    @Test
    void updateProduct() {
//        given
        when(repository.save(any())).thenReturn(storage);

//        when
        service.updateProduct(200, productDuplicateDTO);

//        then
        verify(repository, times(1)).save(any(Storage.class));
    }

    @Test
    void findAllStorageWithQuantity() {
//        given
        when(repository.findAll()).thenReturn(Collections.singletonList(storage));
        when(productClient.getAllProductWithQuantity(anyList())).thenReturn(new ArrayList<>());

//        when
        List<ProductWithQuantityDTO> result = service.findAllStorageWithQuantity();

//        then
        assertEquals(0, result.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void deleteById() {
//        given
        doNothing().when(repository).deleteById(anyLong());

//        when
        service.deleteById(1L);

//        then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void findById() {
//        given
        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

//        when
        Storage result = service.findById(1L);

//        then
        assertEquals(storage, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isInStorage() {
//        given
        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

//        when
        Boolean result = service.isInStorage(1L, 50);

//        then
        assertFalse(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void isOrderInStorage() {
//        given
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 5);
        when(repository.findById(anyLong())).thenReturn(Optional.of(storage));

//        when
        Boolean result = service.isOrderInStorage(cart);

//        then
        assertTrue(result);
    }

    @Test
    void findOutOfStorageProduct() {
//        given
        Map<ProductDuplicateDTO, Integer> cart = new HashMap<>();
        cart.put(productDuplicateDTO, 15);

//        when
        Map<ProductDuplicateDTO, Integer> result = service.findOutOfStorageProduct(cart, "1L");

//        then
        assertTrue(result.containsKey(productDuplicateDTO));
    }

    @Test
    void productVerification() {
//        given
        when(repository.findAll()).thenReturn(List.of(storage));

//        when
        service.productVerification();

//        then
        verify(kafkaProductVerification, times(1)).send(eq("product-name-identifier-topic"), anyList());
    }

    @Test
    void reduceQuantityById() {
//        given
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

//        when
        service.reduceQuantityById(orderDuplicateDTO);

//        then
        verify(entityManager, times(2)).createNativeQuery(anyString());
        verify(queryMock, times(2)).executeUpdate();
    }
}
