package com.invoice.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.invoice.api.dto.DtoProduct;
import com.invoice.exception.ApiException;

@Service
public class SvcProductImp implements SvcProduct {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public DtoProduct getProduct(String gtin) {
        try {
            // Reutilizamos el token de la petición original
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            
            ResponseEntity<DtoProduct> response = restTemplate.exchange(
                    "http://localhost:8080/product/gtin/" + gtin, 
                    HttpMethod.GET, 
                    entity, 
                    DtoProduct.class);
            return response.getBody();
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El producto con GTIN " + gtin + " no existe.");
        }
    }

    @Override
    public void updateProductStock(String gtin, Integer stock) {
        try {
            // Reutilizamos el token de la petición original
            HttpEntity<String> entity = new HttpEntity<>(getHeaders());
            
            restTemplate.exchange(
                    "http://localhost:8080/product/" + gtin + "/stock/" + stock, 
                    HttpMethod.PUT, 
                    entity, 
                    Void.class);
        } catch (Exception e) {
            // Imprimir el error real en consola para depurar si falla
            System.err.println("Error actualizando stock: " + e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo actualizar el stock del producto.");
        }
    }
    
    // Método auxiliar para obtener el header Authorization de la petición actual
    private HttpHeaders getHeaders() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = attributes.getRequest().getHeader("Authorization");
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // Pasamos el Bearer token tal cual
        return headers;
    }
}