package com.shop.storageservice.client;

import com.shop.storageservice.dto.ProductWithQuantityDTO;
import com.shop.storageservice.dto.StorageDuplicateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "product-service", url = "${url.productClient}")
public interface ProductClient {

    @GetMapping("api/v1/product/get/all")
    List<ProductWithQuantityDTO> getAllProductWithQuantity(List<StorageDuplicateDTO> storageList);
}
