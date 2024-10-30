package com.shop.customerservice.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Document(collection = "sale")
public class Sale {

    @Id
    private ObjectId id;
    private Long customerId;
    private BigDecimal sale;
}
