package com.invoice.api.dto;

public class DtoCartItem {
    
    private Integer cart_item_id;
    private String gtin;
    private String product;
    private Double unit_price; 
    private Integer quantity;
    private Double total;

    public DtoCartItem() {}

    public DtoCartItem(Integer cart_item_id, String gtin, String product, Double unit_price, Integer quantity) {
        this.cart_item_id = cart_item_id;
        this.gtin = gtin;
        this.product = product;
        this.unit_price = unit_price;
        this.quantity = quantity;
        this.total = unit_price * quantity;
    }

	public Integer getCart_item_id() {
		return cart_item_id;
	}

	public void setCart_item_id(Integer cart_item_id) {
		this.cart_item_id = cart_item_id;
	}

	public String getGtin() {
		return gtin;
	}

	public void setGtin(String gtin) {
		this.gtin = gtin;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public Double getUnit_price() {
		return unit_price;
	}

	public void setUnit_price(Double unit_price) {
		this.unit_price = unit_price;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}

}