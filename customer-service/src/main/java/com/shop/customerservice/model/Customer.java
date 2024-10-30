package com.shop.customerservice.model;

import com.shop.customerservice.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customer")
public class Customer {

    @Id
    private ObjectId id;

    private String email;

    private String phoneNumber;

    private String nickName;

    @NotBlank(message = "Name can`t be blank")
    private String name;

    @NotBlank(message = "Surname can`t be blank")
    private String surname;

    private Gender gender;

    private LocalDate dateOfBirth;

    private Map<Long, Integer> cart;

    private Boolean newsLetterSubscribe;
}
