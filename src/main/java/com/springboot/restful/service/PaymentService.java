package com.springboot.restful.service;

import java.util.List;

import com.springboot.restful.entity.PaymentEntity;
import com.springboot.restful.model.Payment;
import com.springboot.restful.model.PaymentCancel;
import com.springboot.restful.model.PaymentResponse;

public interface PaymentService {
	
	List<PaymentEntity> findAll();
	PaymentResponse findById(String id) throws Exception;
	PaymentEntity save(Payment payment) throws Exception;
	PaymentEntity save(PaymentCancel paymentCancel) throws Exception;	

}
