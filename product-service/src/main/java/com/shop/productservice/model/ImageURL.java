package com.shop.productservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;

@Entity
@Table(name = "image_url")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageURL {

    @Id
    private Long id;
    private URL ImageURL;


    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
