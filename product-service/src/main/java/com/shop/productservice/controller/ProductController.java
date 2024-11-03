package com.shop.productservice.controller;


import com.shop.productservice.dto.*;
import com.shop.productservice.model.Product;
import com.shop.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    ProductService service;

    @PostMapping("get/all")
    public List<ProductWithQuantityDTO> getAllProductWithQuantity(@RequestBody List<StorageDuplicateDTO> storageList) {
        return service.getAllProductWithQuantity(storageList);
    }

    @PostMapping("name-identifier/group")
    public List<OrderWithProductCartDTO> groupNameIdentifier(@RequestBody List<OrderDuplicateDTO> listOrders) {
        return service.groupNameIdentifier(listOrders);
    }

    @PostMapping("create")
    public Product createProduct(@RequestBody Product product,
                                 @RequestBody List<MultipartFile> photos) throws IOException {
        return service.createProduct(product, photos);
    }



    @PutMapping("update")
    public Product updateProduct(@RequestBody Product product,
                                 @RequestBody List<MultipartFile> photos) throws IOException {
        return service.updateProduct(product, photos);
    }

    @DeleteMapping("delete/{id}")
    public void deleteProductById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping("name-identifier")
    public List<ProductDuplicateDTO> nameIdentifier(@RequestBody List<Long> listId) {
        return service.nameIdentifier(listId);
    }

    @GetMapping("share/{slug}")
    public Product shareProduct(@PathVariable String slug) {
        return service.findBySlug(slug);
    }


    @GetMapping("get/{id}")
    public Product getProductById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("get/category/{category}")
    public List<Product> findProductByCategory(@PathVariable String category) {
        return service.findAllByCategory(category);
    }
}
