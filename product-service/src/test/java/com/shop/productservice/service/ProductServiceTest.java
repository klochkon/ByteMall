package com.shop.productservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.shop.productservice.dto.MailDTO;
import com.shop.productservice.dto.ProductWithQuantityDTO;
import com.shop.productservice.dto.StorageDuplicateDTO;
import com.shop.productservice.model.ImageURL;
import com.shop.productservice.model.Product;
import com.shop.productservice.repository.ImageURLRepository;
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
    private ProductRepository productRepository;

    @Mock
    private ImageURLRepository urlRepository;

    @Mock
    private AmazonS3 amazonS3;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaTemplate;

    private Product product;
    private List<MultipartFile> photos;

    @BeforeEach
    void setUp() throws IOException {

        List<ImageURL> urls = new ArrayList<>();
        ImageURL imageURL = ImageURL.builder()
                .id(1L)
                .ImageURL(new URL("http://test.com"))
                .build();
        urls.add(imageURL);
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .category("Electronics")
                .cost(new BigDecimal("100.0"))
                .description("Test Description")
                .imageUrl(urls)
                .build();

        productService.setBucketName("bucketName");

        photos = new ArrayList<>();
        photos.add(new MockMultipartFile("photo1", "photo1.jpg", "image/jpeg", new ByteArrayInputStream("content1".getBytes())));
        photos.add(new MockMultipartFile("photo2", "photo2.jpg", "image/jpeg", new ByteArrayInputStream("content2".getBytes())));
        photos.add(new MockMultipartFile("photo3", "photo3.jpg", "image/jpeg", new ByteArrayInputStream("content3".getBytes())));
    }

    @Test
    void createProductTest() throws IOException {


        doReturn(new URL("http://mock-s3-url.com/photo1.jpg")).when(amazonS3).getUrl(anyString(), anyString());

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.createProduct(product, photos);

        verify(amazonS3, times(photos.size())).putObject(anyString(), anyString(), any(), any());
        verify(urlRepository, times(photos.size())).save(any(ImageURL.class));
        verify(productRepository).save(product);

        assertEquals(photos.size(), savedProduct.getImageUrl().size());
        assertEquals("Test Product", savedProduct.getName());
    }

    @Test
    void findById() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        Product foundProduct = productService.findById(1L);

        assertNotNull(foundProduct);
        assertEquals(product.getId(), foundProduct.getId());
    }

    @Test
    void deleteById() {
        when(productRepository.findById(1L)).thenReturn(java.util.Optional.of(product));

        productService.deleteById(1L);

        verify(amazonS3).deleteObject(anyString(), anyString());
        verify(productRepository).deleteById(1L);
    }

    @Test
    void getAllProductWithQuantity() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

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
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateProduct(product, photos);

        assertNotNull(updatedProduct);
        assertEquals(product.getId(), updatedProduct.getId());
        verify(amazonS3, times(3)).putObject(anyString(), anyString(), any(), isNull());
    }
}
