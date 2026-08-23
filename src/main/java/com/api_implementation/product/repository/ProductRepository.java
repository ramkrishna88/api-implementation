package com.api_implementation.product.repository;

import com.api_implementation.product.modal.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();

    @Query("SELECT p FROM Product p " +
            "WHERE p.active = true " +
            "AND p.stockQuantity > 0 " +
            "AND lower(p.name) LIKE lower(concat('%', :keyword, '%'))")
    List<Product> searchProductBy(@Param("keyword") String keyword);
}
