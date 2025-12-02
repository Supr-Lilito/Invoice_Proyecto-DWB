package com.invoice.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoCartItem;
import com.invoice.api.entity.CartItem;
import com.invoice.api.service.SvcCart;
import com.invoice.commons.util.JwtDecoder;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart-item")
public class CtrlCart {

    @Autowired
    SvcCart svc;
    
    @Autowired
    JwtDecoder jwtDecoder; 

    @GetMapping
    public ResponseEntity<List<DtoCartItem>> getCart(){
        Integer userId = jwtDecoder.getUserId();
        return ResponseEntity.ok(svc.getCart(userId));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addToCart(@Valid @RequestBody CartItem item){
        Integer userId = jwtDecoder.getUserId();
        return ResponseEntity.ok(svc.addToCart(userId, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> removeFromCart(@PathVariable("id") Integer id){
        Integer userId = jwtDecoder.getUserId();
        return ResponseEntity.ok(svc.removeFromCart(userId, id));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> clearCart(){
        Integer userId = jwtDecoder.getUserId();
        return ResponseEntity.ok(svc.clearCart(userId));
    }
}