package com.springboot.restful.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.springboot.restful.entity.PaymentEntity;
import com.springboot.restful.exception.RequestInvalidException;
import com.springboot.restful.exception.ResourceNotFoundException;
import com.springboot.restful.model.DataModel;
import com.springboot.restful.model.Payment;
import com.springboot.restful.model.PaymentCancel;
import com.springboot.restful.model.PaymentResponse;
import com.springboot.restful.repository.PaymentRepository;
import com.springboot.restful.service.PaymentService;
import com.springboot.restful.util.PaymentUtil;

@Service
public class PaymentServiceImpl implements PaymentService {

	private PaymentRepository paymentRepository;
	
	public PaymentServiceImpl(PaymentRepository PaymentRepository) {
		super();
		this.paymentRepository = PaymentRepository;
	}
	
	@Override
	public List<PaymentEntity> findAll() {	
		return paymentRepository.findAll();
	}
	
	@Override
	public PaymentEntity save(Payment payment) throws Exception {
		StringBuffer sb = new StringBuffer();
		// 데이터 길이 취득
		int len = PaymentUtil.getDataLength();
		// 관리번호 생성
		String id = PaymentUtil.idGenerate();
		// 공통헤더부문 편집처리
		sb.append(PaymentUtil.getCommonHeader(len, PaymentUtil.REQUEST_PAYMENT, id));
		// 데이터부문 편집처리
		sb.append(PaymentUtil.getDataField(payment));
		// 엔티티 인스턴스 생성
		PaymentEntity result = new PaymentEntity(id, sb.toString());
		// 레포지토리 퍼시스트 제어
		paymentRepository.save(result);
		
		return result;
	}
	
	@Override
	public PaymentEntity save(PaymentCancel paymentCancel) throws Exception {
		StringBuffer sb = new StringBuffer();
		// 데이터 길이 취득
		int len = PaymentUtil.getDataLength();
		// 관리번호 생성
		String id = PaymentUtil.idGenerate();
		// 공통헤더부문 편집처리
		sb.append(PaymentUtil.getCommonHeader(
				len, PaymentUtil.REQUEST_CANCEL, id));
		// 원거래 관리번호 조회
		String originId = paymentCancel.getId();
		PaymentEntity pe = paymentRepository.findById(originId).orElseThrow(
				()-> new ResourceNotFoundException("Not Found Resources :" + originId));
		DataModel response = PaymentUtil.parseDataField(pe.getInfo());
		// 원거래 관리번호 편집처리
		if (response.getOriginId().isBlank()) {
			response.setOriginId(originId);
		}
		// 거래금액보다 취소금액이 작은지 검정한다.
		int billingAmout = Integer.parseInt(response.getBilling().trim());
		int cancelAmout = Integer.parseInt(paymentCancel.getBilling().trim());
		if ((billingAmout - cancelAmout) < 0) {
			throw new RequestInvalidException("취소금액이 거래금액보다 큽니다.");
		}
		// 취소금액 편집처리
		response.setBilling(paymentCancel.getBilling());
		// 데이터부문 편집처리
		sb.append(PaymentUtil.getDataField(response));
		// 엔티티 인스턴스 생성
		PaymentEntity result = new PaymentEntity(id, sb.toString());
		// 레포지토리 퍼시스트 제어
		paymentRepository.save(result);
				
		return result;
	}

	@Override
	public PaymentResponse findById(String id) throws Exception {
		// 관리번호 참조 조회
		PaymentEntity pe = paymentRepository.findById(id).orElseThrow(
				()-> new ResourceNotFoundException("Not Found Resources :" + id));
		DataModel response = PaymentUtil.parseDataField(pe.getInfo());
		
		PaymentResponse pr = new PaymentResponse();
		pr.setId(id);
		pr.setCardNum(PaymentUtil.maskingCardNum(response.getCardNum().trim()));
		pr.setExpDate(response.getExpDate());
		pr.setCvc(response.getCvc());
		pr.setReq(pe.getInfo().substring(4, 14).trim());
		pr.setBilling(Integer.parseInt(response.getBilling().trim()));
		pr.setVAT(Integer.parseInt(response.getVAT().trim()));
		
		return pr;
	}
}
