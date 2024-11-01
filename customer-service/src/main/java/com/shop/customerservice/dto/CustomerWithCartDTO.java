package com.shop.customerservice.dto;


import com.shop.customerservice.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CustomerWithCartDTO {
    private String id;

    private String email;

    private String phoneNumber;

    private BigDecimal sale;

    private String nickName;

    private String name;

    private String surname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate dateOfBirth;

    private Map<ProductDuplicateDTO, Integer> cart;

    private Boolean newsLetterSubscribe;

}
