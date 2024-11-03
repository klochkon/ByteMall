package com.shop.customerservice.service;

import com.shop.customerservice.dto.SaleDuplicateDTO;
import com.shop.customerservice.model.Sale;
import com.shop.customerservice.repository.SaleRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    private SaleRepository repository;

    @InjectMocks
    private SaleService saleService;

    private Sale sale;
    private SaleDuplicateDTO saleDuplicateDTO;

    @BeforeEach
    void setUp() {
        sale = Sale.builder()
                .id(new ObjectId("id"))
                .customerId("customerId")
                .sale(BigDecimal.valueOf(0.10))
                .build();

        saleDuplicateDTO = SaleDuplicateDTO.builder()
                .id("id")
                .customerId("customerId")
                .sale(BigDecimal.valueOf(0.10))
                .build();
    }

    @Test
    void saveSale() {
        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.saveSale(sale);

        assertEquals(sale, result);
        verify(repository, times(1)).save(sale);
    }

    @Test
    void saveSaleDTO() {
        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.saveSaleDTO(saleDuplicateDTO);

        assertEquals(sale, result);
        verify(repository, times(1)).save(any(Sale.class));
    }

    @Test
    void updateSale() {
        when(repository.save(any(Sale.class))).thenReturn(sale);

        Sale result = saleService.updateSale(sale);

        assertEquals(sale, result);
        verify(repository, times(1)).save(sale);
    }

    @Test
    void deleteSaleById() {
        doNothing().when(repository).deleteById(any());

        saleService.deleteSaleById(sale.getId().toHexString());

        verify(repository, times(1)).deleteById(any());
    }

    @Test
    void findSaleById() {
        when(repository.findById(any())).thenReturn(Optional.of(sale));

        Sale result = saleService.findSaleById(sale.getId().toHexString());

        assertEquals(sale, result);
        verify(repository, times(1)).findById(any());
    }

    @Test
    void findSaleById_NotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        Sale result = saleService.findSaleById(sale.getId().toHexString());

        assertNull(result);
        verify(repository, times(1)).findById(any());
    }

    @Test
    void findAllByCustomerId() {
        List<Sale> sales = List.of(sale);
        when(repository.findAllByCustomerId(any())).thenReturn(sales);

        List<Sale> result = saleService.findAllByCustomerId(sale.getId().toHexString());

        assertEquals(sales, result);
        verify(repository, times(1)).findAllByCustomerId(any());
    }
}
