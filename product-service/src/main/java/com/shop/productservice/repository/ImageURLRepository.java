package com.shop.productservice.repository;

import com.shop.productservice.model.ImageURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageURLRepository extends JpaRepository<ImageURL, Long> {
}
