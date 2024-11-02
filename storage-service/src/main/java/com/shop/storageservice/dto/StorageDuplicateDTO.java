package com.shop.storageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class StorageDuplicateDTO {

    private Long productId;
    private Integer quantity;
}
