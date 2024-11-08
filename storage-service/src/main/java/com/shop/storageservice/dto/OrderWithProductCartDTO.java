package com.shop.storageservice.dto;

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
public class OrderWithProductCartDTO {

    private String id;
    private String customerId;
    private Map<ProductDuplicateDTO, Integer> cart;
    private BigDecimal cost;

}