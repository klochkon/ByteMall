package com.shop.storageservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

@FeignClient(name = "customer-service", url = "${url.customerClient}")
public interface CustomerClient {

    @PutMapping("api/v1/customer/identify/email")
    void customerIdentify(Map<Long,String> productsWasOutMap);
}
