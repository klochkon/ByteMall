package com.shop.customerservice.Controller;

import com.shop.customerservice.Model.Sale;
import com.shop.customerservice.Service.SaleService;
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
    public void deleteSaleById(@PathVariable Long id) {
        service.deleteSaleById(id);
    }

    @GetMapping("find/{id}")
    public Sale findSaleById(@PathVariable Long id) {
        return service.findSaleById(id);
    }

    @GetMapping("find/all/{customerId}")
    public List<Sale> findAllByCustomerId(@PathVariable Long customerId) {
        return service.findAllByCustomerId(customerId);
    }
}