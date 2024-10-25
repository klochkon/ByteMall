package com.shop.customerservice.Service;

import com.shop.customerservice.Client.NotificationClient;
import com.shop.customerservice.DTO.CustomerDTO;
import com.shop.customerservice.DTO.MailDTO;
import com.shop.customerservice.Model.Customer;
import com.shop.customerservice.Model.Sale;
import com.shop.customerservice.Repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository repository;
    private final KafkaTemplate<String, MailDTO> kafkaRegistration;
    private final NotificationClient notificationClient;
    private final MongoTemplate mongoTemplate;
    private final SaleService saleService;

    @CachePut(value = {"customer", "allCustomer"}, key = "#customer.id")
    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer: {}", customer);
        Map<String, Object> data = Map.of(
                "Email", customer.getEmail(),
                "Name", customer.getName()
        );

        MailDTO mailDTO = MailDTO.builder()
                .to(customer.getEmail())
                .data(data)
                .build();

        Sale sale = Sale.builder()
                .customerId(customer.getId())
                .sale(new BigDecimal("0.1"))
                .build();

        saleService.saveSale(sale);
        kafkaRegistration.send("mail-topic", mailDTO);

        Customer savedCustomer = repository.save(customer);
        log.info("Customer saved successfully: {}", savedCustomer);
        return savedCustomer;
    }

    @CachePut(value = {"customer", "allCustomer"}, key = "#customer.id")
    public Customer updateCustomer(Customer customer) {
        log.info("Updating customer: {}", customer);
        Customer updatedCustomer = repository.save(customer);
        log.info("Customer updated successfully: {}", updatedCustomer);
        return updatedCustomer;
    }

    @CacheEvict(value = {"customer", "allCustomer"}, key = "#id")
    public void deleteCustomerById(Long id) {
        log.info("Deleting customer with id: {}", id);
        repository.deleteById(id);
        log.info("Customer with id {} deleted successfully", id);
    }

    @Cacheable(value = "customer", key = "#id")
    public Customer findCustomerById(Long id) {
        log.info("Finding customer by id: {}", id);
        Customer customer = repository.findById(id).orElse(null);
        log.info("Customer found: {}", customer);
        return customer;
    }

    @Cacheable(value = "allCustomer")
    public List<Customer> findAllCustomer() {
        log.info("Finding all customers");
        List<Customer> customers = repository.findAll();
        log.info("Total customers found: {}", customers.size());
        return customers;
    }

    public CustomerDTO findCustomerEmailAndNameById(Long customerId) {
        log.info("Finding customer email and name by id: {}", customerId);
        Customer customer = repository.findById(customerId).orElse(null);
        CustomerDTO customerDTO = new CustomerDTO();

        if (customer != null) {
            customerDTO.setEmail(customer.getEmail());
            customerDTO.setName(customer.getName());
            log.info("Found customer: {}", customerDTO);
        } else {
            log.warn("Customer with id {} not found", customerId);
        }

        return customerDTO;
    }

    public void customerIdentify(Map<Long, String> productsWasOutMap) {
        log.info("Identifying customers for products that were out of stock");
        for (Map.Entry<Long, String> entry : productsWasOutMap.entrySet()) {
            Customer customer = repository.findById(entry.getKey()).orElse(null);

            if (customer != null) {
                Map<String, Object> data = Map.of(
                        "Product", entry.getValue(),
                        "Name", customer.getName()
                );
                MailDTO mailDTO = MailDTO.builder()
                        .data(data)
                        .to(customer.getEmail())
                        .build();
                notificationClient.sendUpdateStorageEmail(mailDTO);
                log.info("Sent update email to customer: {}", customer.getEmail());
            } else {
                log.warn("Customer with id {} not found for product: {}", entry.getKey(), entry.getValue());
            }
        }
    }

    public void cleanCart(String id) {
        log.info("Cleaning cart for customer with id: {}", id);
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update().set("cart", Map.of());
        mongoTemplate.updateFirst(query, update, "customer");
        log.info("Cart cleaned for customer with id: {}", id);
    }
}
