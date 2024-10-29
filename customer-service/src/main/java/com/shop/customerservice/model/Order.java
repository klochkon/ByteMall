package com.shop.customerservice.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "order")
public class Order {

    @Id
    private Long id;
    private Long customerId;
    private Map<Long, Integer> cart;
    private BigDecimal cost;
}
