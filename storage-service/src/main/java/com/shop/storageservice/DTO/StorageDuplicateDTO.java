package com.shop.storageservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class StorageDuplicateDTO {

    private Long customerId;
    private Integer quantity;
}
