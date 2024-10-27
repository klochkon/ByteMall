package com.shop.customerservice.Service;

import com.shop.customerservice.Client.NotificationClient;
import com.shop.customerservice.Client.ProductClient;
import com.shop.customerservice.DTO.CustomerDTO;
import com.shop.customerservice.DTO.CustomerWithCartDTO;
import com.shop.customerservice.DTO.MailDTO;
import com.shop.customerservice.DTO.ProductDuplicateDTO;
import com.shop.customerservice.Model.Customer;
import com.shop.customerservice.Model.Sale;
import com.shop.customerservice.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private CustomerWithCartDTO customerWithCartDTO;
    private Sale sale;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        customer = Customer.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .cart(new HashMap<>())
                .build();

        sale = Sale.builder()
                .customerId(customer.getId())
                .sale(new BigDecimal("0.1"))
                .build();

        customerWithCartDTO = CustomerWithCartDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .cart(new HashMap<>())
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
        String customerId = "1";

        doNothing().when(mongoTemplate).updateFirst(any(Query.class), any(Update.class), anyString());

        customerService.cleanCart(customerId);

        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), anyString());
    }
}
