package com.invoice.api.dto;

import com.invoice.api.entity.PaymentInfo;
import com.invoice.api.entity.ShippingAddress;
import jakarta.validation.constraints.NotNull;

public class DtoPurchase {
    
    @NotNull(message = "La dirección de envío es obligatoria")
    private ShippingAddress address;
    
    @NotNull(message = "La información de pago es obligatoria")
    private PaymentInfo payment;
    

	public ShippingAddress getAddress() {
		return address;
	}

	public void setAddress(ShippingAddress address) {
		this.address = address;
	}

	public PaymentInfo getPayment() {
		return payment;
	}

	public void setPayment(PaymentInfo payment) {
		this.payment = payment;
	}
    
}