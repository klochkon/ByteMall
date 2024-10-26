package com.shop.customerservice.Controller;

import com.shop.customerservice.DTO.CustomerDTO;
import com.shop.customerservice.DTO.CustomerWithCartDTO;
import com.shop.customerservice.Model.Customer;
import com.shop.customerservice.Service.CustomerService;
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
    public void deleteCustomer(@PathVariable Long id) {
        service.deleteCustomerById(id);
    }

    @GetMapping("find/{id}")
    public CustomerWithCartDTO findCustomerById(@PathVariable Long id) {
        return service.findCustomerById(id);
    }

    @GetMapping("find/customerDTO/{customerId}")
    public CustomerDTO findCustomerEmailAndNameById(@PathVariable Long customerId) {
        return service.findCustomerEmailAndNameById(customerId);
    }

    @GetMapping("find/all")
    public List<Customer> findAllCustomer() {
        return service.findAllCustomer();
    }

    @PutMapping("clean/cart/{id}")
    public void cleanCart(@PathVariable String id) {
        service.cleanCart(id);
    }

    @PutMapping("identify/email")
    public void customerIdentify(@RequestBody Map<Long,String> productsWasOutMap) {
        service.customerIdentify(productsWasOutMap);
    }

}
