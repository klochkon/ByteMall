package com.shop.storageservice.Repository;

import com.shop.storageservice.Model.Storage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface StorageRepository extends JpaRepository<Storage, Long> {

    @Modifying
    @Query(value = "UPDATE Storage" +
            "SET quantity = quantity + quantityAdded" +
            "WHERE id = addedId",
            nativeQuery = true)
    void addProductById(Long addedId, Integer quantityAdded);

    @Modifying
    @Query(value = "UPDATE Storage" +
            "SET quantity = quantity - quantityDeleted" +
            "WHERE id = deletedId",
            nativeQuery = true)
    void deleteProductById(Long deletedId, Integer quantityDeleted);
}
