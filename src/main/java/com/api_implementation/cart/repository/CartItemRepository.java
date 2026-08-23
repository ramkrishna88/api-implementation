package com.api_implementation.cart.repository;

import com.api_implementation.cart.modal.CartItem;
import com.api_implementation.product.modal.Product;
import com.api_implementation.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUserAndProduct(User user, Product product);

    List<CartItem> findByUser(User user);

    void deleteByUserAndProduct(User user, Product product);
}
