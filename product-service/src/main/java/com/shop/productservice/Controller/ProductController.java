package com.shop.productservice.Controller;


import com.shop.productservice.DTO.ProductWithQuantityDTO;
import com.shop.productservice.DTO.StorageDuplicateDTO;
import com.shop.productservice.Model.Product;
import com.shop.productservice.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.channels.MulticastChannel;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
public class ProductController {

    ProductService service;

    @GetMapping("get/all")
    public List<ProductWithQuantityDTO> getAllProductWithQuantity(@RequestBody List<StorageDuplicateDTO> storageList) {
        return service.getAllProductWithQuantity(storageList);
    }

    @PostMapping("create")
    public Product createProduct(@RequestBody Product product,
                                 @RequestParam MultipartFile photo) throws IOException {
        return service.createProduct(product, photo);
    }



    @PutMapping("update")
    public Product updateProduct(@RequestBody Product product,
                                 @RequestParam MultipartFile photo) throws IOException {
        return service.updateProduct(product, photo);
    }

    @DeleteMapping("delete/{id}")
    public void deleteProductById(@PathVariable Long id) {
        service.deleteById(id);
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
