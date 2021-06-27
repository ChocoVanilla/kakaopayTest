package com.springboot.restful.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataModel {

	// 관리번호
	private String id;
	// 결제취소구분
	private String req;
	// 카드번호
	private String cardNum;
	// 할부개월수
	private String monthlyInst;
	// 카드유효기간
	private String expDate;
	// CVC
	private String cvc;
	// 결제금액
	private String billing;
	// 부가가치세
	private String VAT;
	// 원거래관리번호
	private String originId;
	// 암호화된 카드정보
	private String encryptCard;
	
}
