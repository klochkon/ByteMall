package com.shop.customerservice.controller;

import com.shop.customerservice.dto.OrderWithProductCartDTO;
import com.shop.customerservice.model.Order;
import com.shop.customerservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/order")
public class OrderController {

    private final OrderService service;

    @PostMapping("save")
    public Order saveOrder(@RequestBody OrderWithProductCartDTO orderDuplicateDTO) {
        return service.saveOrder(orderDuplicateDTO);
    }

    @PutMapping("update")
    public Order updateOrder(Order order) {
        return service.updateOrder(order);
    }

    @DeleteMapping("delete/{id}")
    public void deleteOrderById(@PathVariable Long id) {
        service.deleteOrderById(id);
    }

    @GetMapping("find/{id}")
    public OrderWithProductCartDTO findById(@PathVariable Long id) {
        return service.findOrderById(id);
    }

    @GetMapping("find/{customerId}")
    public List<OrderWithProductCartDTO> findByCustomerId(@PathVariable Long customerId) {
        return service.findAllByCustomerId(customerId);
    }
}
