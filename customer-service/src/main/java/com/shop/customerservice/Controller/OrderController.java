package com.shop.customerservice.Controller;

import com.shop.customerservice.DTO.OrderWithProductCartDTO;
import com.shop.customerservice.Model.Order;
import com.shop.customerservice.Service.OrderService;
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
    public Order findById(@PathVariable Long id) {
        return service.findOrderById(id);
    }

    @GetMapping("find/{customerId}")
    public List<Order> findByCustomerId(@PathVariable Long customerId) {
        return service.findAllByCustomerId(customerId);
    }
}
