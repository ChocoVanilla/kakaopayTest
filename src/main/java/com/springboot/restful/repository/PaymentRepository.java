package com.springboot.restful.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.restful.entity.PaymentEntity;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
	
}
	