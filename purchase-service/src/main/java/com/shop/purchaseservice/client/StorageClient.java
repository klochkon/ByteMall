package com.shop.purchaseservice.client;

import com.shop.purchaseservice.dto.ProductDuplicateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "storage-service", url = "${url.storageClient}")
public interface StorageClient {

    @PostMapping("api/v1/storage/order-check")
    Boolean isOrderInStorage(@RequestBody Map<ProductDuplicateDTO, Integer> cart);

    @GetMapping("api/v1/storage/find/order/out")
    Map<ProductDuplicateDTO, Integer> findOutOfStorageProduct(
            @RequestBody Map<ProductDuplicateDTO, Integer> cart, @RequestParam String customerId);
}
