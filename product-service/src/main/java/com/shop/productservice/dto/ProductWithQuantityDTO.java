package com.shop.productservice.dto;

import com.shop.productservice.model.ImageURL;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ProductWithQuantityDTO {

    private Long id;

    private String description;
    private BigDecimal cost;
    private String name;
    private String producer;
    private String category;

    private BigDecimal feedBack;

    private Integer quantity;

    private List<ImageURL> imageURLS;
}
