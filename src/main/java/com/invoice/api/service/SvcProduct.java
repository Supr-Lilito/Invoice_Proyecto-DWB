package com.invoice.api.service;

import com.invoice.api.dto.DtoProduct;

public interface SvcProduct {
    public DtoProduct getProduct(String gtin);
    public void updateProductStock(String gtin, Integer stock);
}