package com.shop.storageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDuplicateDTO {

    private Long id;

    private String description;
    private BigDecimal cost;
    private String name;
    private String producer;
    private String category;

    private BigDecimal feedBack;

}