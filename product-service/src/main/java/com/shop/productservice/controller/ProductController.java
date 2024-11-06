package com.shop.productservice.controller;


import com.shop.productservice.dto.*;
import com.shop.productservice.model.Product;
import com.shop.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService service;

    @PostMapping("get/all")
    public List<ProductWithQuantityDTO> getAllProductWithQuantity(@RequestBody List<StorageDuplicateDTO> storageList) {
        return service.getAllProductWithQuantity(storageList);
    }

    @PostMapping("name-identifier/group")
    public List<OrderWithProductCartDTO> groupNameIdentifier(@RequestBody List<OrderDuplicateDTO> listOrders) {
        return service.groupNameIdentifier(listOrders);
    }

    @PostMapping(value = "create", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Product createProduct(@RequestBody ProductSaveRequest request) throws IOException {
        return service.createProduct(request.getProduct(), request.getPhotos());
    }



    @PutMapping(value = "update", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Product updateProduct(@RequestBody ProductSaveRequest request) throws IOException {
        return service.updateProduct(request.getProduct(), request.getPhotos());
    }

    @DeleteMapping("delete/{id}")
    public void deleteProductById(@PathVariable(name = "id") Long id) {
        service.deleteById(id);
    }

    @PostMapping("name-identifier")
    public List<ProductDuplicateDTO> nameIdentifier(@RequestBody List<Long> listId) {
        return service.nameIdentifier(listId);
    }

    @GetMapping("share/{slug}")
    public Product shareProduct(@PathVariable(name = "slug") String slug) {
        return service.findBySlug(slug);
    }


    @GetMapping("get/{id}")
    public Product getProductById(@PathVariable(name = "id") Long id) {
        return service.findById(id);
    }

    @GetMapping("get/category/{category}")
    public List<Product> findProductByCategory(@PathVariable(name = "category") String category) {
        return service.findAllByCategory(category);
    }
}
