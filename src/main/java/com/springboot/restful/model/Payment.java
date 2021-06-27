package com.springboot.restful.model;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.lang.Nullable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {
	
	@Length(min = 10, max = 20, message = "최소 10자리, 최대 20자리")
	private String cardNum;
	@Range(min = 0, max = 12, message = "0(일시불), 1 ~ 12개월")
	private String monthlyInst;
	@Length(min = 4, max = 4, message = "4자리 숫자")
	private String expDate;
	@Length(min = 3, max = 3, message = "3자리 숫자")
	private String cvc;
	@Range(min = 100, max = 1000000000, message = "100원 이상, 10억원 이하")
	private Integer billing;
	@Nullable
	private Integer VAT;

}
