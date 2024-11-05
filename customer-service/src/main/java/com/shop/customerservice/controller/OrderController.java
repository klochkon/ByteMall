package com.shop.customerservice.controller;

import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {

    private final OrderService service;

    @PostMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Order saveOrder(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        return service.saveOrder(orderDuplicateDTO);
    }

    @PutMapping("update")
    public Order updateOrder(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        return service.updateOrder(orderDuplicateDTO);
    }

    @DeleteMapping("delete/{id}")
    public void deleteOrderById(@PathVariable(name = "id") String id) {
        service.deleteOrderById(id);
    }

    @GetMapping("find/{id}")
    public OrderWithProductCartDTO findById(@PathVariable(name = "id") String id) {
        return service.findOrderById(id);
    }

    @GetMapping("find/customer/{customerId}")
    public List<OrderWithProductCartDTO> findByCustomerId(@PathVariable(name = "customerId") String customerId) {
        return service.findAllByCustomerId(customerId);
    }
}
