package com.springboot.restful.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentCancel {
	
	@Length(min = 20, max = 20, message = "유일 값 20자리")
	private String id;
	@Range(min = 100, max = 1000000000, message = "100원 이상, 10억원 이하")
	private String billing;
}
