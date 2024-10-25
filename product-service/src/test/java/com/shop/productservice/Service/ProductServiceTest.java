package com.shop.productservice.Service;

import com.shop.productservice.DTO.MailDTO;
import com.shop.productservice.DTO.ProductWithQuantityDTO;
import com.shop.productservice.DTO.StorageDuplicateDTO;
import com.shop.productservice.Model.Product;
import com.shop.productservice.Repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaVerification;

    @InjectMocks
    private ProductService productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        product1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .category("Category 1")
                .cost(BigDecimal.valueOf(10.0))
                .description("Description 1")
                .feedBack(BigDecimal.valueOf(4.5))
                .producer("Producer 1")
                .slug("product-1")
                .imageUrl("http://example.com/product1.jpg")
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Product 2")
                .category("Category 2")
                .cost(BigDecimal.valueOf(15.0))
                .description("Description 2")
                .feedBack(BigDecimal.valueOf(4.0))
                .producer("Producer 2")
                .slug("product-2")
                .imageUrl("http://example.com/product2.jpg")
                .build();
    }

    @Test
    void getAllProductWithQuantity() {
        List<Product> products = Arrays.asList(product1, product2);
        List<StorageDuplicateDTO> storageList = Arrays.asList(
                new StorageDuplicateDTO(1L, 5),
                new StorageDuplicateDTO(2L, 3)
        );

        when(productRepository.findAll()).thenReturn(products);

        List<ProductWithQuantityDTO> result = productService.getAllProductWithQuantity(storageList);

        assertEquals(2, result.size());
        assertEquals(5, result.get(0).getQuantity());
        assertEquals(3, result.get(1).getQuantity());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void nameIdentifier() {
        List<Product> products = Arrays.asList(product1, product2);
        List<StorageDuplicateDTO> productsWithLack = Arrays.asList(
                new StorageDuplicateDTO(1L, 5),
                new StorageDuplicateDTO(2L, 3)
        );

        when(productRepository.findAll()).thenReturn(products);

        productService.nameIdentifier(productsWithLack);

        verify(kafkaVerification, times(1)).send(eq("mail-topic"), any(MailDTO.class));
    }

    @Test
    void createProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        Product result = productService.createProduct(product1);

        assertEquals(product1, result);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void deleteById() {
        Long productId = 1L;

        doNothing().when(productRepository).deleteById(productId);

        productService.deleteById(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void findById() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        Product result = productService.findById(productId);

        assertEquals(product1, result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_NullIfNotExists() {
        Long productId = 1L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        Product result = productService.findById(productId);

        assertNull(result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void updateProduct() {
        when(productRepository.save(product1)).thenReturn(product1);

        Product result = productService.updateProduct(product1);

        assertEquals(product1, result);
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void findAllByCategory() {
        String category = "Category 1";
        List<Product> products = Collections.singletonList(product1);

        when(productRepository.findAllByCategory(category)).thenReturn(products);

        List<Product> result = productService.findAllByCategory(category);

        assertEquals(1, result.size());
        assertEquals(product1, result.get(0));
        verify(productRepository, times(1)).findAllByCategory(category);
    }
}
