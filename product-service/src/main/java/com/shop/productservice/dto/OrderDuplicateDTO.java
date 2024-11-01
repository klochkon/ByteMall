package com.shop.productservice.dto;

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

    private String id;
    private String customerId;
    private Map<Long, Integer> cart;
    private BigDecimal cost;
}
