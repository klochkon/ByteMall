package com.shop.productservice.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.shop.productservice.DTO.MailDTO;
import com.shop.productservice.DTO.ProductDuplicateDTO;
import com.shop.productservice.DTO.ProductWithQuantityDTO;
import com.shop.productservice.DTO.StorageDuplicateDTO;
import com.shop.productservice.Model.Product;
import com.shop.productservice.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class ProductService {

    private final ProductRepository repository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    private final KafkaTemplate<String, MailDTO> kafkaVerification;

    @Cacheable(value = "productWithQuantity")
    public List<ProductWithQuantityDTO> getAllProductWithQuantity(List<StorageDuplicateDTO> storageList) {
        List<Product> products = repository.findAll();
        List<ProductWithQuantityDTO> resultList = new ArrayList<>();

        Map<Long, Integer> storageMap = new HashMap<>();
        for (StorageDuplicateDTO storageDuplicateDTO : storageList) {
            storageMap.put(storageDuplicateDTO.getCustomerId(), storageDuplicateDTO.getQuantity());
        }

        for (Product product : products) {
            ProductWithQuantityDTO productWithQuantityDTO = ProductWithQuantityDTO.builder()
                    .quantity(storageMap.get(product.getId()))
                    .id(product.getId())
                    .name(product.getName())
                    .category(product.getCategory())
                    .cost(product.getCost())
                    .description(product.getDescription())
                    .feedBack(product.getFeedBack())
                    .producer(product.getProducer())
                    .build();
            resultList.add(productWithQuantityDTO);
        }
        log.info("Retrieved all products with quantities: {}", resultList);
        return resultList;
    }

//    todo nameIdentifier must be universal
//    @KafkaListener(topics = "product-name-identifier-topic", groupId = "${spring.kafka.consumer-groups.product-name-identifier-group.group-id}")
//    public void nameIdentifier(List<StorageDuplicateDTO> productsWithLack) {
//        MailDTO mailDTO = new MailDTO();
//        Map<String, Object> data = new HashMap<>();
//        List<Product> products = repository.findAll();
//
//        Map<Long, String> productMap = new HashMap<>();
//        for (Product product : products) {
//            productMap.put(product.getId(), product.getName());
//        }
//
//        Map<String, Integer> LackMap = new HashMap<>();
//        for (StorageDuplicateDTO storageDuplicateDTO : productsWithLack) {
//            LackMap.put(productMap.get(storageDuplicateDTO.getCustomerId()), storageDuplicateDTO.getQuantity());
//        }
//
//        data.put("MapOfLackProducts", LackMap);
//        mailDTO.setData(data);
//        kafkaVerification.send("mail-topic", mailDTO);
//        log.info("Sent mailDTO with lack products: {}", mailDTO);
//    }

//    @CachePut(value = {"allProduct", "product"}, key = "#product.id")
//    public Product createProduct(Product product, List<MultipartFile> photos) {
//
//        for (MultipartFile photo : photos) {
//            amazonS3.putObject()
//        }
//
//        Product savedProduct = repository.save(product);
//        log.info("Product created successfully: {}", savedProduct);
//        return savedProduct;
//    }

//    public List<ProductDuplicateDTO> nameIdentifier(List<Long> listId) {
//        List<Product> productsList = repository.findAllById(listId);
//        List <ProductDuplicateDTO> dtoList= new ArrayList<>();
//        for (Product product : productsList) {
//            ProductDuplicateDTO duplicate;
//            duplicate = ProductDuplicateDTO.builder()
//                    .id(product.getId())
//                    .category(product.getCategory())
//                    .cost(product.getCost())
//                    .name(product.getName())
//                    .description(product.getDescription())
//                    .feedBack(product.getFeedBack())
//                    .producer(product.getProducer())
//                    .build();
//        }
//    }

    @CacheEvict(value = {"product", "allProduct"}, key = "#id")
    public void deleteById(Long id) {
        repository.deleteById(id);
        log.info("Product with id {} deleted successfully", id);
    }

    @Cacheable(value = "product", key = "#id")
    public Product findById(Long id) {
        Product product = repository.findById(id).orElse(null);
        log.info("Product found: {}", product);
        return product;
    }

    @Cacheable(value = "product", key = "#slug")
    public Product findBySlug(String slug) {
        Product product = repository.findProductBySlug(slug);
        log.info("Product found by slug '{}': {}", slug, product);
        return product;
    }

    @CachePut(value = {"product", "allProduct"}, key = "#product.id")
    public Product updateProduct(Product product) {
        Product updatedProduct = repository.save(product);
        log.info("Product updated successfully: {}", updatedProduct);
        return updatedProduct;
    }

    @Cacheable(value = "allProduct", key = "#category")
    public List<Product> findAllByCategory(String category) {
        List<Product> products = repository.findAllByCategory(category);
        log.info("Found {} products in category '{}'", products.size(), category);
        return products;
    }
}
