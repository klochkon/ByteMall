package com.shop.productservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    private Long id;
    private String authorNickname;
    private LocalDate dateOfPublishing;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;


}
