package com.shop.purchaseservice.controller;


import com.shop.purchaseservice.dto.InventoryStatusDTO;
import com.shop.purchaseservice.dto.OrderWithProductCartDTO;
import com.shop.purchaseservice.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/purchase")
public class PurchaseController {

    private final PurchaseService service;

    @PostMapping(value = "operation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public InventoryStatusDTO purchase(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        return service.purchase(orderDuplicateDTO);
    }

    @PostMapping(value = "mail/send", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void purchaseMailSend(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        service.purchaseMailSend(orderDuplicateDTO);
    }
}