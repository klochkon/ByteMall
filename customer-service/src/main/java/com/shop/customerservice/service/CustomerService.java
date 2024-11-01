package com.shop.customerservice.service;

import com.shop.customerservice.client.NotificationClient;
import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.CustomerDTO;
import com.shop.customerservice.dto.CustomerWithCartDTO;
import com.shop.customerservice.dto.MailDTO;
import com.shop.customerservice.dto.ProductDuplicateDTO;
import com.shop.customerservice.model.Customer;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerService {

    private final CustomerRepository repository;
    private final KafkaTemplate<String, MailDTO> kafkaRegistration;
    private final NotificationClient notificationClient;
    private final ProductClient productClient;
    private final MongoTemplate mongoTemplate;
    private final SaleService saleService;

    @CachePut(value = {"customer", "allCustomer"}, key = "#customer.id")
    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer: {}", customer);
        Map<String, Object> data = Map.of(
                "Email", customer.getEmail(),
                "Name", customer.getName()
        );
        log.info("Mail data prepared for customer: {}", data);

        MailDTO mailDTO = MailDTO.builder()
                .to(customer.getEmail())
                .data(data)
                .build();
        log.info("MailDTO created: {}", mailDTO);

        Sale sale = Sale.builder()
                .customerId(customer.getId().toHexString())
                .sale(new BigDecimal("0.1"))
                .build();
        log.info("Sale created: {}", sale);

        saleService.saveSale(sale);
        kafkaRegistration.send("mail-topic", mailDTO);
        log.info("Registration email sent to topic for customer: {}", customer.getEmail());

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
    public void deleteCustomerById(String id) {
        log.info("Deleting customer with id: {}", id);
        repository.deleteById(new ObjectId(id));
        log.info("Customer with id {} deleted successfully", id);
    }

    @Cacheable(value = "customer", key = "#id")
    public CustomerWithCartDTO findCustomerById(String id) {
        log.info("Finding customer by id: {}", id);
        Customer customer = repository.findById(new ObjectId(id)).orElse(null);
        log.info("Customer found: {}", customer);

        if (customer != null) {
            List<Long> listId = new ArrayList<>();
            List<Integer> listQuantity = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : customer.getCart().entrySet()) {
                listId.add(entry.getKey());
                listQuantity.add(entry.getValue());
            }
            log.info("Customer cart product IDs: {}", listId);
            log.info("Customer cart quantities: {}", listQuantity);

            List<ProductDuplicateDTO> listProducts = productClient.nameIdentifier(listId);
            Map<ProductDuplicateDTO, Integer> cartWithProduct = new HashMap<>();
            for (ProductDuplicateDTO product : listProducts) {
                cartWithProduct.put(product, listQuantity.remove(0));
            }
            CustomerWithCartDTO customerWithCartDTO = CustomerWithCartDTO.builder()
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .gender(customer.getGender())
                    .dateOfBirth(customer.getDateOfBirth())
                    .newsLetterSubscribe(customer.getNewsLetterSubscribe())
                    .surname(customer.getName())
                    .nickName(customer.getNickName())
                    .phoneNumber(customer.getPhoneNumber())
                    .id(customer.getId().toHexString())
                    .cart(cartWithProduct)
                    .build();

            log.info("CustomerWithCartDTO created: {}", customerWithCartDTO);
            return customerWithCartDTO;
        }
        return null;
    }

    @Cacheable(value = "allCustomer")
    public List<Customer> findAllCustomer() {
        log.info("Finding all customers");
        List<Customer> customers = repository.findAll();
        log.info("Total customers found: {}", customers.size());
        return customers;
    }

    public CustomerDTO findCustomerEmailAndNameById(String customerId) {
        log.info("Finding customer email and name by id: {}", customerId);
        Customer customer = repository.findById(new ObjectId(customerId)).orElse(null);
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

    public void customerIdentify(Map<String, String> productsWasOutMap) {
        log.info("Identifying customers for products that were out of stock");
        for (Map.Entry<String, String> entry : productsWasOutMap.entrySet()) {
            Customer customer = repository.findById(new ObjectId(entry.getKey())).orElse(null);

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
        Query query = new Query(Criteria.where("id").is(new ObjectId(id)));
        Update update = new Update().set("cart", Map.of());
        log.info("Executing cart clean update for customer with id: {}", id);
        mongoTemplate.updateFirst(query, update, "customer");
        log.info("Cart cleaned for customer with id: {}", id);
    }
}
