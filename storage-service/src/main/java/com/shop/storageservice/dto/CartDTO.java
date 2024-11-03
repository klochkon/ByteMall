package com.shop.storageservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@Data
public class CartDTO {
    private Map<ProductDuplicateDTO, Integer> cart;
}
