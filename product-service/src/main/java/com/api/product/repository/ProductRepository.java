package com.api.product.repository;

import com.api.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND p.stockQuantity > 0 "
            + "AND lower(p.name) LIKE lower(concat('%', :keyword, '%'))")
    List<Product> searchProductBy(@Param("keyword") String keyword);
}
