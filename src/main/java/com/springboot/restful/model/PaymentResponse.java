package com.springboot.restful.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentResponse {

	private String id;
	private String cardNum;
	private String expDate;
	private String cvc;
	private String req;
	private Integer billing;
	private Integer VAT;

}	
