package com.invoice.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoCartItem;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.CartItem;
import com.invoice.api.repository.RepoCart;
import com.invoice.exception.ApiException;

@Service
public class SvcCartImp implements SvcCart {

    @Autowired
    RepoCart repo;
    
    @Autowired
    SvcProduct svcProduct; 

    @Override
    public List<DtoCartItem> getCart(Integer userId) {
        List<CartItem> items = repo.findByUserId(userId);
        List<DtoCartItem> dtoItems = new ArrayList<>();

        for (CartItem item : items) {
            // Buscamos datos frescos del producto para cumplir la Nota 4
            DtoProduct product = svcProduct.getProduct(item.getGtin());
            
            DtoCartItem dto = new DtoCartItem(
                item.getCart_item_id(),
                item.getGtin(),
                product.getProduct(), // Nombre obtenido del otro API
                product.getPrice(),   // Precio obtenido del otro API
                item.getQuantity()
            );
            
            dtoItems.add(dto);
        }
        return dtoItems;
    }

    @Override
    public ApiResponse addToCart(Integer userId, CartItem item) {
        // 1. Validar que el producto exista 
        DtoProduct product = svcProduct.getProduct(item.getGtin());
        
        // 2. Validar stock suficiente 
        if (product.getStock() < item.getQuantity()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No hay stock suficiente. Disponible: " + product.getStock());
        }

        // 3. Verificar si ya existe en el carrito 
        Optional<CartItem> existingItem = repo.findByUserIdAndGtin(userId, item.getGtin());
        
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            // Validar que la suma no exceda el stock
            if (product.getStock() < (cartItem.getQuantity() + item.getQuantity())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Stock insuficiente para agregar esa cantidad extra.");
            }
            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
            repo.save(cartItem);
        } else {
            item.setUser_id(userId);
            item.setStatus(1);
            repo.save(item);
        }

        return new ApiResponse("Artículo agregado al carrito");
    }

    @Override
    public ApiResponse removeFromCart(Integer userId, Integer itemId) {
        Optional<CartItem> item = repo.findById(itemId);
        if(item.isPresent() && item.get().getUser_id().equals(userId)) {
             repo.removeFromCart(itemId);
             return new ApiResponse("Artículo eliminado del carrito");
        }
        throw new ApiException(HttpStatus.NOT_FOUND, "El artículo no existe en su carrito");
    }

    @Override
    public ApiResponse clearCart(Integer userId) {
        repo.clearCart(userId);
        return new ApiResponse("Carrito vaciado");
    }
}