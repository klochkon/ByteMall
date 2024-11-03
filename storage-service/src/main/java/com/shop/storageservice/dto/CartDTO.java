package com.shop.storageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CartDTO {
    private Map<ProductDuplicateDTO, Integer> cart;
}
