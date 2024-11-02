package com.shop.customerservice.controller;

import com.shop.customerservice.model.Sale;
import com.shop.customerservice.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/sale")
public class SaleController {

    private final SaleService service;

    @PutMapping("update")
    public Sale updateSale(@RequestBody Sale sale) {
        return service.updateSale(sale);
    }

    @PostMapping("save")
    public Sale saveSale(@RequestBody Sale sale) {
        return service.saveSale(sale);
    }

    @DeleteMapping("delete/{id}")
    public void deleteSaleById(@PathVariable String id) {
        service.deleteSaleById(id);
    }

    @GetMapping("find/{id}")
    public Sale findSaleById(@PathVariable String id) {
        return service.findSaleById(id);
    }

    @GetMapping("find/all/{customerId}")
    public List<Sale> findAllByCustomerId(@PathVariable String customerId) {
        return service.findAllByCustomerId(customerId);
    }
}