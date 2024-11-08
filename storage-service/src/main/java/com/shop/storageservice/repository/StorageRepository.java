package com.shop.storageservice.repository;

import com.shop.storageservice.model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Modifying
    @Query(value = "UPDATE Storage " +
            "SET quantity = quantity + :quantityAdded " +
            "WHERE id = :addedId",
            nativeQuery = true)
    void raiseProductQuantityById(Long addedId, Integer quantityAdded);
}
