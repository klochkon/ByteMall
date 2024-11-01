package com.shop.customerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

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
