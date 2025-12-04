package com.invoice.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "payment_info")
public class PaymentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer payment_id;

    @JsonIgnore
    @Column(name = "invoice_id")
    private Integer invoice_id;

    @Column(name = "payment_method")
    private String payment_method;

    @Column(name = "card_last4")
    private String card_last4;

    @Column(name = "amount")
    private Double amount;
    

	public Integer getPayment_id() {
		return payment_id;
	}

	public void setPayment_id(Integer payment_id) {
		this.payment_id = payment_id;
	}

	public Integer getInvoice_id() {
		return invoice_id;
	}

	public void setInvoice_id(Integer invoice_id) {
		this.invoice_id = invoice_id;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}

	public String getCard_last4() {
		return card_last4;
	}

	public void setCard_last4(String card_last4) {
		this.card_last4 = card_last4;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

    
}