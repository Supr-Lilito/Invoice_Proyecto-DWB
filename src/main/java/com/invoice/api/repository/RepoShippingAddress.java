package com.invoice.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.invoice.api.entity.ShippingAddress;

@Repository
public interface RepoShippingAddress extends JpaRepository<ShippingAddress, Integer> {}