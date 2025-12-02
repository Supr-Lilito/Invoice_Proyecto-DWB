package com.invoice.api.service;

import java.util.List;
import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoCartItem; 
import com.invoice.api.entity.CartItem;

public interface SvcCart {
    public List<DtoCartItem> getCart(Integer userId);
    public ApiResponse addToCart(Integer userId, CartItem item);
    public ApiResponse removeFromCart(Integer userId, Integer itemId);
    public ApiResponse clearCart(Integer userId);
}