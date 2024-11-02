package com.shop.storageservice.dto;

import lombok.Data;
import java.util.Map;

@Data
public class CartDTO {
    private Map<ProductDuplicateDTO, Integer> cart;
}
