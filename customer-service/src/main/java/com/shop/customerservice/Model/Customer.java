package com.shop.customerservice.Model;

import com.shop.customerservice.DTO.ProductDuplicateDTO;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer")
public class Customer {

    @Id
    private Long id;

    private String email;

    private String phoneNumber;

    private BigDecimal sale;

    private String nickName;

    @NotBlank(message = "Name can`t be blank")
    private String name;

    @NotBlank(message = "Surname can`t be blank")
    private String surname;

    private String sex;

    private LocalDate dateOfBirth;

//    todo all places where in db dto do id and nameIdentifier
    private Map<Long, Integer> cart;

    private Boolean newsLetterSubscribe;
}
