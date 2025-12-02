package com.invoice.api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.invoice.api.dto.ApiResponse;
import com.invoice.api.dto.DtoInvoiceList;
import com.invoice.api.dto.DtoProduct;
import com.invoice.api.entity.CartItem;
import com.invoice.api.entity.Invoice;
import com.invoice.api.entity.InvoiceItem;
import com.invoice.api.repository.RepoCart;
import com.invoice.api.repository.RepoInvoice;
import com.invoice.api.repository.RepoInvoiceItem;
import com.invoice.commons.mapper.MapperInvoice;
import com.invoice.commons.util.JwtDecoder;
import com.invoice.exception.ApiException;
import com.invoice.exception.DBAccessException;

import jakarta.transaction.Transactional;

@Service
public class SvcInvoiceImp implements SvcInvoice {

    @Autowired
    private RepoInvoice repo;
    
    @Autowired
    private RepoInvoiceItem repoItem;
    
    @Autowired
    private RepoCart repoCart;
    
    @Autowired
    private SvcProduct svcProduct;
    
    @Autowired
    private JwtDecoder jwtDecoder;
    
    @Autowired
    MapperInvoice mapper;

    @Override
    public List<DtoInvoiceList> findAll() {
        try {
            if(jwtDecoder.isAdmin()) {
                return mapper.toDtoList(repo.findAll());
            } else {
                Integer user_id = jwtDecoder.getUserId();
                return mapper.toDtoList(repo.findAllByUserId(user_id));
            }
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }

    @Override
    public Invoice findById(Integer id) {
        try {
            Invoice invoice = repo.findById(id).get();
            if(!jwtDecoder.isAdmin()) {
                Integer user_id = jwtDecoder.getUserId();
                if(invoice.getUser_id() != user_id) {
                    throw new ApiException(HttpStatus.FORBIDDEN, "El token no es válido para consultar esta factura");
                }
            }
            return invoice;
        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        } catch (NoSuchElementException e) {
            throw new ApiException(HttpStatus.NOT_FOUND, "El id de la factura no existe");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public ApiResponse create() {
        try {
            Integer userId = jwtDecoder.getUserId();
            
            // 1. Identificar artículos del carrito
            List<CartItem> cartItems = repoCart.findByUserId(userId);
            if (cartItems.isEmpty()) {
                throw new ApiException(HttpStatus.NOT_FOUND, "El carrito está vacío");
            }

            // 2. Crear y GUARDAR la factura inicial para obtener el ID
            Invoice invoice = new Invoice();
            invoice.setUser_id(userId);
            invoice.setCreated_at(LocalDateTime.now().toString());
            invoice.setStatus(1);
            invoice.setSubtotal(0.0);
            invoice.setTaxes(0.0);
            invoice.setTotal(0.0);
            
            // ¡ESTE ES EL CAMBIO CLAVE! Guardamos primero para generar el ID
            invoice = repo.save(invoice); 

            // Variables para acumular totales
            Double total = 0.0;
            Double taxes = 0.0;
            Double subtotal = 0.0;
            
            List<InvoiceItem> invoiceItems = new ArrayList<>();

            for (CartItem item : cartItems) {
                // Validar stock y obtener precio
                DtoProduct product = svcProduct.getProduct(item.getGtin());
                if (product.getStock() < item.getQuantity()) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Stock insuficiente para: " + product.getProduct());
                }

                // Calcular montos
                Double itemTotal = item.getQuantity() * product.getPrice();
                Double itemTaxes = itemTotal * 0.16;
                Double itemSubtotal = itemTotal - itemTaxes;

                // Crear Item usando el ID de la factura YA CREADA
                InvoiceItem invoiceItem = new InvoiceItem();
                invoiceItem.setInvoice_id(invoice.getInvoice_id()); // Ahora esto NO es null
                invoiceItem.setGtin(item.getGtin());
                invoiceItem.setQuantity(item.getQuantity());
                invoiceItem.setUnit_price(product.getPrice());
                invoiceItem.setTotal(itemTotal);
                invoiceItem.setTaxes(itemTaxes);
                invoiceItem.setSubtotal(itemSubtotal);
                invoiceItem.setStatus(1);
                
                invoiceItems.add(invoiceItem);
                
                total += itemTotal;
                taxes += itemTaxes;
                subtotal += itemSubtotal;

                // Actualizar stock
                svcProduct.updateProductStock(item.getGtin(), product.getStock() - item.getQuantity());
            }

            // 3. Guardar todos los items de golpe
            repoItem.saveAll(invoiceItems);

            // 4. Actualizar la factura con los totales finales
            invoice.setTotal(total);
            invoice.setTaxes(taxes);
            invoice.setSubtotal(subtotal);
            invoice.setItems(invoiceItems);
            
            repo.save(invoice); // Guardamos la actualización

            // 5. Vaciar carrito
            repoCart.clearCart(userId);

            return new ApiResponse("Compra realizada con éxito");

        } catch (DataAccessException e) {
            throw new DBAccessException(e);
        }
    }
}