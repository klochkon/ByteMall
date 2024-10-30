package com.shop.purchaseservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryStatusDTO {

    public Boolean isOrderInStorage;
    private Map<ProductDuplicateDTO, Integer> outOfStorageProducts;


}
