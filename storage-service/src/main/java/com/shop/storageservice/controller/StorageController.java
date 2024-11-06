package com.shop.storageservice.controller;

import com.shop.storageservice.dto.CartDTO;
import com.shop.storageservice.dto.OrderWithProductCartDTO;
import com.shop.storageservice.dto.ProductDuplicateDTO;
import com.shop.storageservice.dto.ProductWithQuantityDTO;
import com.shop.storageservice.model.Storage;
import com.shop.storageservice.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService service;

    @GetMapping("check")
    public Boolean isInStorage(@RequestParam Long id, @RequestParam Integer requiredQuantity) {
        return service.isInStorage(id, requiredQuantity);
    }

    @PostMapping("save/{quantity}")
    public void saveProduct(@PathVariable Integer quantity,
                            @RequestBody ProductDuplicateDTO productDuplicateDTO) {
        service.saveProduct(quantity, productDuplicateDTO);
    }

    @PutMapping("save/{quantity}")
    public void updateProduct(@PathVariable Integer quantity,
                              @RequestBody ProductDuplicateDTO productDuplicateDTO) {
        service.updateProduct(quantity, productDuplicateDTO);
    }


    @GetMapping("find/{id}")
    public Storage findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @DeleteMapping("delete/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

    @PostMapping("add")
    public void raiseProductQuantityById(
            @RequestBody ProductDuplicateDTO productDuplicateDTO,
            @RequestParam Integer quantityAdded) {
        service.raiseProductQuantityById(productDuplicateDTO, quantityAdded);
    }

    @PutMapping("delete")
    public void reduceQuantityById(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        service.reduceQuantityById(orderDuplicateDTO);
    }

    @PostMapping(value = "check/order",consumes = {"application/json", "application/json;charset=UTF-8"}, produces = "application/json")
    public Boolean isOrderInStorage(@RequestBody CartDTO cart) {
        return service.isOrderInStorage(cart.getCart());
    }

    @PostMapping("find/order/out/{customerId}")
    public Map<ProductDuplicateDTO, Integer> findOutOfStorageProduct(
            @RequestBody CartDTO cart,
            @PathVariable(name = "customerId") String customerId) {
        return service.findOutOfStorageProduct(cart.getCart(), customerId);
    }

    @GetMapping("find/all")
    public List<ProductWithQuantityDTO> findAllStorageWithQuantity() {
        return service.findAllStorageWithQuantity();
    }

}
