package com.shop.productservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.shop.productservice.dto.*;
import com.shop.productservice.model.ImageURL;
import com.shop.productservice.model.Product;
import com.shop.productservice.repository.ImageURLRepository;
import com.shop.productservice.repository.ProductRepository;
import lombok.Data;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Data
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ImageURLRepository urlRepository;
    private final AmazonS3 amazonS3;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    private final KafkaTemplate<String, MailDTO> kafkaVerification;

    @Cacheable(value = "productWithQuantity")
    public List<ProductWithQuantityDTO> getAllProductWithQuantity(List<StorageDuplicateDTO> storageList) {
        log.info("Fetching all products with quantity.");
        List<Product> products = productRepository.findAll();
        List<ProductWithQuantityDTO> resultList = new ArrayList<>();

        Map<String, Integer> storageMap = new HashMap<>();
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
                    .imageURLS(product.getImageUrl())
                    .build();
            resultList.add(productWithQuantityDTO);
        }
        log.info("Retrieved all products with quantities: {}", resultList);
        return resultList;
    }

    @KafkaListener(topics = "product-name-identifier-topic", groupId = "${spring.kafka.consumer-groups.product-name-identifier-group.group-id}")
    public void productVerification(List<StorageDuplicateDTO> productsWithLack) {
        log.info("Received products with lack for verification: {}", productsWithLack);
        MailDTO mailDTO = new MailDTO();
        Map<String, Object> data = new HashMap<>();
        List<Product> products = productRepository.findAll();

        Map<Long, String> productMap = new HashMap<>();
        for (Product product : products) {
            productMap.put(product.getId(), product.getName());
        }

        Map<String, Integer> LackMap = new HashMap<>();
        for (StorageDuplicateDTO storageDuplicateDTO : productsWithLack) {
            LackMap.put(productMap.get(storageDuplicateDTO.getCustomerId()), storageDuplicateDTO.getQuantity());
        }

        data.put("MapOfLackProducts", LackMap);
        mailDTO.setData(data);
        kafkaVerification.send("mail-topic", mailDTO);
        log.info("Sent mailDTO with lack products: {}", mailDTO);
    }

    @CachePut(value = {"allProduct", "product"}, key = "#product.id")
    public Product createProduct(Product product, List<MultipartFile> photos) throws IOException {
        log.info("Creating product with ID: {}, Name: {}", product.getId(), product.getName());
        List<ImageURL> urls = new ArrayList<>();
        Integer numberOfIterations = 0;

        for (MultipartFile photo : photos) {
            numberOfIterations++;
            StringBuilder photoName = new StringBuilder();
            photoName.append(product.getName());
            photoName.append("_");
            photoName.append(numberOfIterations);

            amazonS3.putObject(bucketName, photoName.toString(), photo.getInputStream(), null);

            ImageURL url = ImageURL.builder()
                    .product(product)
                    .ImageURL(amazonS3.getUrl(bucketName, photoName.toString()))
                    .build();

            urlRepository.save(url);
            urls.add(url);
        }

        product.setImageUrl(urls);
        log.info("Product photo(s) for '{}' uploaded to bucket: {}", product.getName(), urls.size());

        Product savedProduct = productRepository.save(product);
        log.info("Product created successfully with ID: {}, Name: {}", savedProduct.getId(), savedProduct.getName());

        return savedProduct;
    }


    public List<ProductDuplicateDTO> nameIdentifier(List<Long> listId) {
        log.info("Identifying names for product IDs: {}", listId);
        List<Product> productsList = productRepository.findAllById(listId);
        List<ProductDuplicateDTO> dtoList = new ArrayList<>();
        for (Product product : productsList) {
            ProductDuplicateDTO duplicate = ProductDuplicateDTO.builder()
                    .id(product.getId())
                    .category(product.getCategory())
                    .cost(product.getCost())
                    .name(product.getName())
                    .description(product.getDescription())
                    .feedBack(product.getFeedBack())
                    .producer(product.getProducer())
                    .imageUrl(product.getImageUrl())
                    .slug(product.getSlug())
                    .build();
            dtoList.add(duplicate);
        }
        Map<Long, ProductDuplicateDTO> entityMap = dtoList.stream()
                .collect(Collectors.toMap(ProductDuplicateDTO::getId, entity -> entity));
        List<ProductDuplicateDTO> result = listId.stream()
                .map(entityMap::get)
                .collect(Collectors.toList());
        log.info("Identified names for products: {}", result);
        return result;
    }

    public List<OrderWithProductCartDTO> groupNameIdentifier(List<OrderDuplicateDTO> listOrders) {
        log.info("Grouping names for order identifiers: {}", listOrders);
        List<OrderWithProductCartDTO> resultList = new ArrayList<>();
        for (OrderDuplicateDTO orderDuplicateDTO : listOrders) {
            List<Long> listId = new ArrayList<>();
            List<Integer> listQuantity = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : orderDuplicateDTO.getCart().entrySet()) {
                listId.add(entry.getKey());
                listQuantity.add(entry.getValue());
            }
            List<ProductDuplicateDTO> listProducts = this.nameIdentifier(listId);
            Map<ProductDuplicateDTO, Integer> cartWithProduct = new HashMap<>();
            for (ProductDuplicateDTO product : listProducts) {
                cartWithProduct.put(product, listQuantity.remove(0));
            }
            OrderWithProductCartDTO orderWithProductCartDTO = OrderWithProductCartDTO.builder()
                    .customerId(orderDuplicateDTO.getCustomerId())
                    .id(orderDuplicateDTO.getId())
                    .cost(orderDuplicateDTO.getCost())
                    .cart(cartWithProduct)
                    .build();
            resultList.add(orderWithProductCartDTO);
        }
        log.info("Grouped names for order identifiers result: {}", resultList);
        return resultList;
    }

    @CacheEvict(value = {"product", "allProduct"}, key = "#id")
    public void deleteById(Long id) {
        log.info("Deleting product by ID: {}", id);
        Product product = productRepository.findById(id).orElse(null);
        Integer numberOfIterations = 0;
        for (ImageURL url : product.getImageUrl()) {
            numberOfIterations++;
            String photoName = product.getName() +
                    "_" +
                    numberOfIterations;
            amazonS3.deleteObject(bucketName, photoName);
        }
        log.info("Product photo with name: {} deleted from bucket", product.getName());
        productRepository.deleteById(id);
        log.info("Product with id {} deleted successfully", id);
    }

    @Cacheable(value = "product", key = "#id")
    public Product findById(Long id) {
        log.info("Finding product by ID: {}", id);
        Product product = productRepository.findById(id).orElse(null);
        log.info("Product found: {}", product);
        return product;
    }

    @Cacheable(value = "product", key = "#slug")
    public Product findBySlug(String slug) {
        log.info("Finding product by slug: {}", slug);
        Product product = productRepository.findProductBySlug(slug);
        log.info("Product found by slug '{}': {}", slug, product);
        return product;
    }

    @CachePut(value = {"product", "allProduct"}, key = "#product.id")
    public Product updateProduct(Product product, List<MultipartFile> photos) throws IOException {
        log.info("Updating product with ID: {}, Name: {}", product.getId(), product.getName());

        Product updatedProduct = this.createProduct(product, photos);

        log.info("Product updated successfully with ID: {}, Name: {}", updatedProduct.getId(), updatedProduct.getName());

        return updatedProduct;
    }


    @Cacheable(value = "allProduct", key = "#category")
    public List<Product> findAllByCategory(String category) {
        log.info("Finding all products by category: {}", category);
        List<Product> products = productRepository.findAllByCategory(category);
        log.info("Found {} products in category '{}'", products.size(), category);
        return products;
    }
}
