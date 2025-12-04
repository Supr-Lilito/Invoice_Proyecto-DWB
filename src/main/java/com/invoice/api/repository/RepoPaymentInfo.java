package com.invoice.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.api.entity.PaymentInfo;

@Repository
public interface RepoPaymentInfo extends JpaRepository<PaymentInfo, Integer> {}