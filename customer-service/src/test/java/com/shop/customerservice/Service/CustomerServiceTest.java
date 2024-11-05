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
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private KafkaTemplate<String, MailDTO> kafkaTemplate;

    @Mock
    private NotificationClient notificationClient;

    @Mock
    private ProductClient productClient;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private SaleService saleService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(new ObjectId())
                .name("John Doe")
                .email("john.doe@example.com")
                .cart(new HashMap<>())
                .gender(Gender.FEMALE)
                .nickName("nickName")
                .newsLetterSubscribe(true)
                .phoneNumber("+380")
                .surname("surname")
                .build();
    }

    @Test
    void saveCustomerTest() {

        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer savedCustomer = customerService.saveCustomer(customer);

        assertEquals(customer.getId(), savedCustomer.getId());
        verify(saleService).saveSale(any(Sale.class));
        verify(kafkaTemplate).send(eq("mail-topic"), any(MailDTO.class));
    }

    @Test
    void updateCustomerTest() {
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(customer);

        assertEquals(customer.getId(), updatedCustomer.getId());
        verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomerByIdTest() {
        customerService.deleteCustomerById(customer.getId().toHexString());

        verify(customerRepository).deleteById(new ObjectId(customer.getId().toHexString()));
    }

    @Test
    void findCustomerByIdTest() {
        when(customerRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(customer));
        when(productClient.nameIdentifier(anyList())).thenReturn(Collections.emptyList());

        CustomerWithCartDTO foundCustomer = customerService.findCustomerById(customer.getId().toHexString());

        assertNotNull(foundCustomer);
        assertEquals(customer.getName(), foundCustomer.getName());
    }

    @Test
    void findAllCustomerTest() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<Customer> customers = customerService.findAllCustomer();

        assertEquals(1, customers.size());
        assertEquals(customer.getId(), customers.get(0).getId());
    }

    @Test
    void findCustomerEmailAndNameByIdTest() {
        when(customerRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(customer));

        CustomerDTO customerDTO = customerService.findCustomerEmailAndNameById(customer.getId().toHexString());

        assertEquals(customer.getEmail(), customerDTO.getEmail());
        assertEquals(customer.getName(), customerDTO.getName());
    }

    @Test
    void customerIdentifyTest() {
        Map<String, String> productsOutMap = Map.of("507f1f77bcf86cd799439011", "ProductA");

        when(customerRepository.findById(any(ObjectId.class))).thenReturn(Optional.of(customer));

        customerService.customerIdentify(productsOutMap);

        ArgumentCaptor<MailDTO> mailCaptor = ArgumentCaptor.forClass(MailDTO.class);
        verify(notificationClient).sendUpdateStorageEmail(mailCaptor.capture());

        assertEquals(customer.getEmail(), mailCaptor.getValue().getTo());
        assertEquals("ProductA", mailCaptor.getValue().getData().get("Product"));
    }

    @Test
    void cleanCartTest() {
        customerService.cleanCart(customer.getId().toHexString());

        verify(mongoTemplate).updateFirst(any(), any(), eq("customer"));
    }
}
