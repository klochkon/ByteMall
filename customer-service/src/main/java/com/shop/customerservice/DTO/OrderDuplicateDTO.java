package com.shop.customerservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDuplicateDTO {

    private Long id;
    private Long customerId;
    private Map<Long, Integer> cart;
    private BigDecimal cost;
}
