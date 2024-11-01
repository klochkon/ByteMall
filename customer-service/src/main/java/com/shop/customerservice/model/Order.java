package com.shop.customerservice.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
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
    private ObjectId id;
    private String customerId;
    private Map<Long, Integer> cart;
    private BigDecimal cost;
}
