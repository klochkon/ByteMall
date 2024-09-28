package com.shop.customerservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SaleDuplicateDTO {

    private Long id;
    private Long customerId;
    private BigDecimal sale;
}
