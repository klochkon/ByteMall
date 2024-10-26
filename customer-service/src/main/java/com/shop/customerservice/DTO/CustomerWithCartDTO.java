package com.shop.customerservice.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerWithCartDTO {
    private Long id;

    private String email;

    private String phoneNumber;

    private BigDecimal sale;

    private String nickName;

    private String name;

    private String surname;

    private String sex;

    private LocalDate dateOfBirth;

    private Map<ProductDuplicateDTO, Integer> cart;

    private Boolean newsLetterSubscribe;

}
