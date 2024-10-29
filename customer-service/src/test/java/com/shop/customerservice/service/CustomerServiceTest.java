package com.shop.customerservice.service;

import com.shop.customerservice.client.NotificationClient;
import com.shop.customerservice.client.ProductClient;
import com.shop.customerservice.dto.CustomerDTO;
import com.shop.customerservice.dto.CustomerWithCartDTO;
import com.shop.customerservice.dto.MailDTO;
import com.shop.customerservice.enums.Gender;
import com.shop.customerservice.model.Customer;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository repository;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaRegistration;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SaleService saleService;

    private Customer customer;
    private Sale sale;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = Customer.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .cart(new HashMap<>())
                .gender(Gender.OTHER)
                .newsLetterSubscribe(true)
                .surname("surname")
                .phoneNumber("123")
                .nickName("nickname")
                .build();

        sale = Sale.builder()
                .id(1L)
                .customerId(customer.getId())
                .sale(new BigDecimal("0.1"))
                .build();
    }

    @Test
    public void testSaveCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);
        when(saleService.saveSale(any(Sale.class))).thenReturn(sale);

        Customer savedCustomer = customerService.saveCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals(1L, savedCustomer.getId());
        verify(repository, times(1)).save(any(Customer.class));
        verify(saleService, times(1)).saveSale(any(Sale.class));
        verify(kafkaRegistration, times(1)).send(anyString(), any(MailDTO.class));
    }

    @Test
    public void testUpdateCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(customer);

        assertNotNull(updatedCustomer);
        assertEquals(1L, updatedCustomer.getId());
        verify(repository, times(1)).save(any(Customer.class));
    }

    @Test
    public void testDeleteCustomerById() {
        Long customerId = 1L;

        doNothing().when(repository).deleteById(anyLong());

        customerService.deleteCustomerById(customerId);

        verify(repository, times(1)).deleteById(customerId);
    }

    @Test
    public void testFindCustomerById() {
        when(repository.findById(anyLong())).thenReturn(java.util.Optional.of(customer));
        when(productClient.nameIdentifier(any())).thenReturn(new ArrayList<>());

        CustomerWithCartDTO foundCustomer = customerService.findCustomerById(1L);

        assertNotNull(foundCustomer);
        assertEquals(1L, foundCustomer.getId());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindAllCustomer() {
        when(repository.findAll()).thenReturn(List.of(customer));

        List<Customer> customers = customerService.findAllCustomer();

        assertNotNull(customers);
        assertEquals(1, customers.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    public void testFindCustomerEmailAndNameById() {
        when(repository.findById(anyLong())).thenReturn(java.util.Optional.of(customer));

        CustomerDTO customerDTO = customerService.findCustomerEmailAndNameById(1L);

        assertNotNull(customerDTO);
        assertEquals("test@example.com", customerDTO.getEmail());
        assertEquals("Test User", customerDTO.getName());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    public void testCustomerIdentify() {
        Map<Long, String> productsWasOutMap = new HashMap<>();
        productsWasOutMap.put(1L, "Product 1");

        when(repository.findById(anyLong())).thenReturn(java.util.Optional.of(customer));

        customerService.customerIdentify(productsWasOutMap);

        verify(notificationClient, times(1)).sendUpdateStorageEmail(any(MailDTO.class));
    }

    @Test
    public void testCleanCart() {
        String customerId = "12345";
        Query expectedQuery = new Query(Criteria.where("id").is(customerId));
        Update expectedUpdate = new Update().set("cart", Map.of());


        customerService.cleanCart(customerId);


        verify(mongoTemplate).updateFirst(eq(expectedQuery), eq(expectedUpdate), eq("customer"));
    }
}
