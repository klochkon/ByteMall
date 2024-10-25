package com.shop.customerservice.Service;

import com.shop.customerservice.Client.NotificationClient;
import com.shop.customerservice.DTO.CustomerDTO;
import com.shop.customerservice.DTO.MailDTO;
import com.shop.customerservice.Model.Customer;
import com.shop.customerservice.Model.Sale;
import com.shop.customerservice.Repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository repository;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaRegistration;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SaleService saleService;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = Customer.builder()
                .id(1L)
                .email("test@test.com")
                .name("John")
                .surname("Doe")
                .sale(BigDecimal.valueOf(0.1))
                .nickName("JD")
                .build();
    }

    @Test
    void saveCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.saveCustomer(customer);

        assertEquals(customer, result);
        verify(repository, times(1)).save(customer);
        verify(saleService, times(1)).saveSale(any(Sale.class));
        verify(kafkaRegistration, times(1)).send(eq("mail-topic"), any(MailDTO.class));
    }

    @Test
    void updateCustomer() {
        when(repository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.updateCustomer(customer);

        assertEquals(customer, result);
        verify(repository, times(1)).save(customer);
    }

    @Test
    void deleteCustomerById() {
        doNothing().when(repository).deleteById(anyLong());

        customerService.deleteCustomerById(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void findCustomerById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(customer));

        Customer result = customerService.findCustomerById(1L);

        assertEquals(customer, result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findCustomerById_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        Customer result = customerService.findCustomerById(1L);

        assertNull(result);
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void findAllCustomer() {
        List<Customer> customers = List.of(customer);
        when(repository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.findAllCustomer();

        assertEquals(customers, result);
        verify(repository, times(1)).findAll();
    }

    @Test
    void findCustomerEmailAndNameById() {
        when(repository.findById(anyLong())).thenReturn(Optional.of(customer));

        CustomerDTO result = customerService.findCustomerEmailAndNameById(1L);

        assertEquals(customer.getEmail(), result.getEmail());
        assertEquals(customer.getName(), result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void customerIdentify() {
        Map<Long, String> productsWasOutMap = Map.of(1L, "Product A");
        when(repository.findById(anyLong())).thenReturn(Optional.of(customer));

        customerService.customerIdentify(productsWasOutMap);

        verify(notificationClient, times(1)).sendUpdateStorageEmail(any(MailDTO.class));
    }

    @Test
    void cleanCart() {
        doNothing().when(mongoTemplate).updateFirst(any(Query.class), any(Update.class), eq("customer"));

        customerService.cleanCart("1");

        verify(mongoTemplate, times(1)).updateFirst(any(Query.class), any(Update.class), eq("customer"));
    }
}
