package com.invoice.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.transaction.Transactional;

import com.invoice.api.entity.CartItem;

@Repository
public interface RepoCart extends JpaRepository<CartItem, Integer> {

    @Query(value = "SELECT * FROM cart_item WHERE user_id = :user_id AND status = 1", nativeQuery = true)
    List<CartItem> findByUserId(@Param("user_id") Integer user_id);

    @Query(value = "SELECT * FROM cart_item WHERE user_id = :user_id AND gtin = :gtin AND status = 1", nativeQuery = true)
    Optional<CartItem> findByUserIdAndGtin(@Param("user_id") Integer user_id, @Param("gtin") String gtin);

    @Modifying
    @Transactional
    @Query(value = "UPDATE cart_item SET status = 0 WHERE user_id = :user_id", nativeQuery = true)
    void clearCart(@Param("user_id") Integer user_id);
    
    @Modifying
    @Transactional
    @Query(value = "UPDATE cart_item SET status = 0 WHERE cart_item_id = :cart_item_id", nativeQuery = true)
    void removeFromCart(@Param("cart_item_id") Integer cart_item_id);
}