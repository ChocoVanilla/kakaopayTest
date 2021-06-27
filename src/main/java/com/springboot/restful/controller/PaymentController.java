package com.springboot.restful.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.restful.entity.PaymentEntity;
import com.springboot.restful.model.Payment;
import com.springboot.restful.model.PaymentCancel;
import com.springboot.restful.model.PaymentResponse;
import com.springboot.restful.service.PaymentService;

@RestController
@RequestMapping("/kakaopay/")
public class PaymentController {
	
	private PaymentService paymentService;
	
	public PaymentController(PaymentService paymentService) {
		super();
		this.paymentService = paymentService; 
	}
	
	@GetMapping("/getPayments")
	public List<PaymentEntity> getPayments() {
		return paymentService.findAll();
	}
	
	@GetMapping("{id}")
	public ResponseEntity<PaymentResponse> getPaymentById(
			@PathVariable(value = "id", required = true) String id) throws Exception {
		
		return ResponseEntity.ok().body(paymentService.findById(id));
	}
	
	@PostMapping("/createPayment")
	public ResponseEntity<PaymentEntity> createPayment(
			@Validated @RequestBody Payment payment) throws Exception {
		
		return ResponseEntity.ok(paymentService.save(payment));
	}
	
	@PostMapping("/cancelPayment")
	public ResponseEntity<PaymentEntity> cancelPayment(
			@Validated @RequestBody PaymentCancel paymentCancel) throws Exception {
		
		return ResponseEntity.ok(paymentService.save(paymentCancel));
	}
}
