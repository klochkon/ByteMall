package com.shop.productservice.repository;

import com.shop.productservice.model.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM comment " +
            "WHERE product_id = :productId", nativeQuery = true)
    List<Comment> findAllByProductId(@Param(value = "productId") Long productId);

    @Query(value = "SELECT * FROM comment " +
            "WHERE author_nickname = :authorNickname", nativeQuery = true)
    List<Comment> findAllByAuthorNickname(@Param(value = "authorNickname") String authorNickname);


}
