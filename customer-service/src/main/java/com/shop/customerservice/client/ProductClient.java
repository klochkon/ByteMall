package com.shop.customerservice.client;

import com.shop.customerservice.dto.OrderDuplicateDTO;
import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.dto.ProductDuplicateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service", url = "${url.productClient}")
public interface ProductClient {

    @GetMapping("api/v1/product/name-identifier")
    List<ProductDuplicateDTO> nameIdentifier(@RequestBody List<Long> listId);

    @GetMapping("api/v1/product/name-identifier/group")
    List<OrderWithProductCartDTO> groupNameIdentifier(@RequestBody List<OrderDuplicateDTO> listOrders);
}
