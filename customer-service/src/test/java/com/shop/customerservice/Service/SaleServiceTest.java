package com.shop.customerservice.service;

import com.shop.customerservice.dto.SaleDuplicateDTO;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.repository.SaleRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @InjectMocks
    private SaleService saleService;

    @Mock
    private SaleRepository repository;

    private Sale sale;

    @BeforeEach
    void setUp() {
        sale = Sale.builder()
                .id(new ObjectId())
                .customerId("customerId")
                .sale(new BigDecimal("100.00"))
                .build();
    }

    @Test
    void saveSale() {
        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale savedSale = saleService.saveSale(sale);

        verify(repository).save(sale);
        assertNotNull(savedSale);
        assertEquals(sale.getId(), savedSale.getId());
    }

    @Test
    void updateSale() {
        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale updatedSale = saleService.updateSale(sale);

        verify(repository).save(sale);
        assertNotNull(updatedSale);
        assertEquals(sale.getId(), updatedSale.getId());
    }

    @Test
    void deleteSaleById() {
        doNothing().when(repository).deleteById(any(ObjectId.class));

        saleService.deleteSaleById(sale.getId().toHexString());

        verify(repository).deleteById(sale.getId());
    }

    @Test
    void findSaleById() {
        when(repository.findById(any(ObjectId.class))).thenReturn(Optional.of(sale));

        Sale foundSale = saleService.findSaleById(sale.getId().toHexString());

        assertNotNull(foundSale);
        assertEquals(sale.getId(), foundSale.getId());
    }

    @Test
    void findAllByCustomerId() {
        when(repository.findAllByCustomerId(any(ObjectId.class))).thenReturn(List.of(sale));

        List<Sale> sales = saleService.findAllByCustomerId(new ObjectId().toHexString());

        assertNotNull(sales);
        assertEquals(1, sales.size());
        assertEquals(sale.getId(), sales.get(0).getId());
    }

    @Test
    void saveSaleDTO() {
        SaleDuplicateDTO saleDuplicateDTO = new SaleDuplicateDTO();
        saleDuplicateDTO.setId(sale.getId().toHexString());
        saleDuplicateDTO.setSale(new BigDecimal("100.0"));
        saleDuplicateDTO.setCustomerId("customerId");

        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale savedSale = saleService.saveSaleDTO(saleDuplicateDTO);

        verify(repository).save(any(Sale.class));
        assertNotNull(savedSale);
        assertEquals(sale.getId(), savedSale.getId());
    }
}
