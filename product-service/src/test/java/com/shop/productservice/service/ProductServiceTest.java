package com.shop.productservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.shop.productservice.dto.MailDTO;
import com.shop.productservice.dto.ProductWithQuantityDTO;
import com.shop.productservice.dto.StorageDuplicateDTO;
import com.shop.productservice.model.Product;
import com.shop.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository repository;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaTemplate;

    private Product product;
    private List<MultipartFile> photos;

    @BeforeEach
    void setUp() throws IOException {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .cost(new BigDecimal("100.0"))
                .description("Test Description")

                .build();

        photos = new ArrayList<>();
        photos.add(new MockMultipartFile("photo1", "photo1.jpg", "image/jpeg", new ByteArrayInputStream("content1".getBytes())));
        photos.add(new MockMultipartFile("photo2", "photo2.jpg", "image/jpeg", new ByteArrayInputStream("content2".getBytes())));
        photos.add(new MockMultipartFile("photo3", "photo3.jpg", "image/jpeg", new ByteArrayInputStream("content3".getBytes())));
    }

    @Test
    void createProduct() throws IOException {
        List<URL> someURL = List.of(new URL("http://example.com"), new URL("http://example2.com"));
        when(repository.save(any(Product.class))).thenReturn(product);
        productService.setBucketName("BucketName");
        when(amazonS3.getUrl(anyString(), anyString())).thenReturn(someURL);

        Product savedProduct = productService.createProduct(product, photos);

        assertEquals(product.getImageUrl(), "http://example.com");
        assertNotNull(savedProduct);
        assertEquals(product.getId(), savedProduct.getId());
        verify(amazonS3, times(1)).putObject(anyString(), anyString(), any(), isNull());
    }

    @Test
    void findById() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(product));

        Product foundProduct = productService.findById(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
    }

    @Test
    void deleteById() {
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(product));

        productService.deleteById(1L);

        verify(amazonS3).deleteObject(anyString(), anyString());
        verify(repository).deleteById(1L);
    }

    @Test
    void getAllProductWithQuantity() {
        when(repository.findAll()).thenReturn(Collections.singletonList(product));

        List<ProductWithQuantityDTO> result = productService.getAllProductWithQuantity(Collections.emptyList());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(product.getId(), result.get(0).getId());
    }

    @Test
    void productVerification() {
        StorageDuplicateDTO storageDuplicateDTO = new StorageDuplicateDTO();
        storageDuplicateDTO.setCustomerId(1L);
        storageDuplicateDTO.setQuantity(10);

        productService.productVerification(Collections.singletonList(storageDuplicateDTO));

        ArgumentCaptor<MailDTO> mailDTOCaptor = ArgumentCaptor.forClass(MailDTO.class);
        verify(kafkaTemplate).send(anyString(), mailDTOCaptor.capture());
        assertNotNull(mailDTOCaptor.getValue());
    }

    @Test
    void updateProduct() throws IOException {
        when(repository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(product, photos);

        assertNotNull(updatedProduct);
        assertEquals(product.getId(), updatedProduct.getId());
        verify(amazonS3).putObject(anyString(), anyString(), any(), isNull());
    }
}
