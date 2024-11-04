package com.shop.customerservice.controller;

import com.shop.customerservice.dto.CustomerDTO;
import com.shop.customerservice.dto.CustomerWithCartDTO;
import com.shop.customerservice.model.Customer;
import com.shop.customerservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping("save")
    public Customer saveCustomer(@RequestBody Customer customer) {
        return service.saveCustomer(customer);
    }

    @PutMapping("update")
    public Customer updateCustomer(@RequestBody Customer customer) {
        return service.updateCustomer(customer);
    }

    @DeleteMapping("delete/{id}")
    public void deleteCustomer(@PathVariable(name = "id") String id) {
        service.deleteCustomerById(id);
    }

    @GetMapping("find/{id}")
    public CustomerWithCartDTO findCustomerById(@PathVariable(name = "id") String id) {
        return service.findCustomerById(id);
    }

    @GetMapping("find/customerDTO/{customerId}")
    public CustomerDTO findCustomerEmailAndNameById(@PathVariable(name = "customerId") String customerId) {
        return service.findCustomerEmailAndNameById(customerId);
    }

    @GetMapping("find/all")
    public List<Customer> findAllCustomer() {
        return service.findAllCustomer();
    }

    @PutMapping("clean/cart/{id}")
    public void cleanCart(@PathVariable(name = "id") String id) {
        service.cleanCart(id);
    }

    @PutMapping("identify/email")
    public void customerIdentify(@RequestBody Map<String,String> productsWasOutMap) {
        service.customerIdentify(productsWasOutMap);
    }

}
