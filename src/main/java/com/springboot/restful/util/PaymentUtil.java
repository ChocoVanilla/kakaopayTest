package com.springboot.restful.util;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.Column;

import com.springboot.restful.entity.PaymentEntity;
import com.springboot.restful.exception.RequestInvalidException;
import com.springboot.restful.model.Payment;
import com.springboot.restful.model.DataModel;


public class PaymentUtil {
	
	// 결제 및 취소 요청코드
	public final static String REQUEST_PAYMENT = "PAYMENT";
	public final static String REQUEST_CANCEL = "CANCEL";
	
	// 데이터 타입 변환 요청코드
	public static final int TYPE_NUMERIC_PREFIX_SPACE = 1;
	public static final int TYPE_NUMERIC_PREFIX_ZERO = 2;
	public static final int TYPE_NUMERIC_SUFFIX_SPACE = 3;
	public static final int TYPE_STRING_SUFFIX_SPACE = 4;
	
	// AES128 비밀암호키 (숫자, 대문자 및 소문자, 마침표, 하이픈 및 밑줄 허용)
	final static String SECURE_KEY = "123.kakaoPayCodingTest-PaymentK_";
	// 암호화 알고리즘, 운용방식, 패딩방식 정의
	final static String CIPHER_TRANSFORM_TYPE = "AES/CBC/PKCS5Padding";
	// 문자열 인코딩 방식 정의
	final static Charset ENCODING_TYPE = StandardCharsets.UTF_8;

	
	private PaymentUtil() {}
	/**
	 * AES 암복호화 비밀암호키 생성 및 반환
	 * 양방향 암호화 알고리즘에서 일반적으로 고정키 방식, 키스토어 방식이 사용된다.
	 * 키스토어의 경우, SecretKey와 Iv를 저장한 외부파일관리 또는 DB관리가 필요하다.
	 */
	public static SecretKey getAESKey() {
        byte[] keyBytes = SECURE_KEY.getBytes(ENCODING_TYPE);
        return new SecretKeySpec(keyBytes, "AES");
    }

	/**
	 * CBC 초기화백터(Iv)생성 및 반환
	 * 바이트 배열에는 {0x00,0x01,0x02,0x03,0x04,0x05,...0x15} 초기값으로 설정  
	 * SecureRandom 적용에는 각 자바 구현체에서 다른 알고리즘이 되어 다른 키가 제공 될 가능성에 주의.
	 */
    public static IvParameterSpec getIv() {
        byte[] iv = new byte[16];
        return new IvParameterSpec(iv);
    }
	
    /**
	 * 문자열 암호화처리 
	 * AES의 Block size는 128 bits, Key size는 128, 192, 256 bits로 사용 가능하다.
	 * EBC모드보다 CBC, CTR같은 보안모드를 사용하는 편이 좋다.
	 * DES는 블록 크기가 64비트, AES는 128, 256비트이므로 최신 알고리즘을 적용할수록 강력하다.
	 */  
	public static String encrypt(SecretKey key, IvParameterSpec iv, String plainText) 
			throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM_TYPE);
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		byte[] result = cipher.doFinal(plainText.getBytes(ENCODING_TYPE));
		return new String(Base64.getEncoder().encode(result));
	}
	
	/**
	 * 문자열 복호화처리
	 * AES 암호화 알고리즘은 대칭키 방식이므로 복호화에서 동일한 키가 필요하다.
	 */
	public static String decrypt(SecretKey key, IvParameterSpec iv, String cipherText)
			throws Exception {
		Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORM_TYPE);
		cipher.init(Cipher.DECRYPT_MODE, key, iv);
		byte[] result = cipher.doFinal(Base64.getDecoder().decode(cipherText));
		return new String(result, ENCODING_TYPE);
	}
	
	/**
	 * 공통헤더부문 데이터 생성 및 반환
	 * @throws Exception 
	 */
	public static String getCommonHeader(int len, String req, String id)
			throws Exception {
		//데이터 길이 : "데이터 길이"를 제외한 총 길이
		//데이터 구분 : 기능 구분값, 승인(PAYMENT), 취소(CANCEL)
		//데이터 관리번호 : 유일 값 20자리
		StringBuffer result = new StringBuffer();
		
		result.append(typeAlignment(
				TYPE_NUMERIC_PREFIX_SPACE, "4", String.valueOf(len)));
		result.append(typeAlignment(
				TYPE_STRING_SUFFIX_SPACE, "10", req));
		result.append(typeAlignment(
				TYPE_STRING_SUFFIX_SPACE, "20", id));
		
		return result.toString();
	}
	
	/**
	 * 데이터부문 생성 및 반환
	 * @throws  
	 */
	public static String getDataField(Object data) throws Exception {
		// 카드번호 : 카드번호 최소 10자리, 최대 20자리
		// 할부개월수 : 일시불, 2개월 ~ 12개월, 취소시에는 일시불 "00"로 저장
		// 카드유효기간 : 월(2자리), 년도(2자리)
		// CVC : 카드 CVC데이터
		// 거래금액 : 결제 : 100원 이상, 취소 : 결제 금액보다 작아야 함
		// 부가가치세 : 결제/취소 금액의 부가세 
		// 원거래관리번호 : 결제시에는 공백
		// 암호화된카드정보 : 카드번호, 유호기간, CVC 데이터를 안전하게 암호화
		// 예비필드 : 향후 생길 데이터를 위해 남겨두는 공간
		StringBuffer result = new StringBuffer();
		if (data instanceof Payment) {
			Payment payment = (Payment) data;
			result.append(
					typeAlignment(TYPE_STRING_SUFFIX_SPACE,
							"20", String.valueOf(payment.getCardNum())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"2", String.valueOf(payment.getMonthlyInst())));
			
			// 카드유효기간이 MMYY형식인지 확인한다.
			SimpleDateFormat df = new SimpleDateFormat("MMyy");
			df.setLenient(false);
			try {
				df.parse(payment.getExpDate());
			} catch (Exception e) {
				throw new RequestInvalidException("MMYY 형식이 아닙니다.");
			}
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"4", String.valueOf(payment.getExpDate())));
			result.append(
					typeAlignment(TYPE_NUMERIC_SUFFIX_SPACE,
							"3", String.valueOf(payment.getCvc())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_SPACE,
							"10", String.valueOf(payment.getBilling())));
			// 부가가치세는 옵션항목이므로 예외처리를 해둔다.
			if (payment.getVAT() == null || payment.getVAT() < 0) {
				payment.setVAT(0);
			}
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"10", String.valueOf(payment.getVAT())));
			result.append(String.format("%20s", ""));
			// 암호화된 카드정보 : 카드번호, CVS, 유효기간을 묶어서 관리한다.
			final String seperate = "|";
			StringBuffer sb = new StringBuffer();
			sb.append(payment.getCardNum());
			sb.append(seperate);
			sb.append(payment.getExpDate());
			sb.append(seperate);
			sb.append(payment.getCvc());
			
			String encrypt = null;
			try {
				encrypt = encrypt(getAESKey(), getIv(), sb.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
						
			result.append(
					typeAlignment(TYPE_STRING_SUFFIX_SPACE,
							"300", String.valueOf(encrypt)));
			result.append(String.format("%47s", " "));
			
		} else if (data instanceof DataModel) {
			DataModel response = (DataModel) data;
			
			result.append(
					typeAlignment(TYPE_STRING_SUFFIX_SPACE,
							"20", String.valueOf(response.getCardNum())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"2", String.valueOf(response.getMonthlyInst())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"4", String.valueOf(response.getExpDate())));
			result.append(
					typeAlignment(TYPE_NUMERIC_SUFFIX_SPACE,
							"3", String.valueOf(response.getCvc())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_SPACE,
							"10", String.valueOf(response.getBilling())));
			result.append(
					typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"10", String.valueOf(response.getVAT())));
			// 원거래 관리번호는 거래금액 입력값을 그대로 입력받는다.
			result.append(response.getOriginId());
			result.append(
					typeAlignment(TYPE_STRING_SUFFIX_SPACE,
							"300", String.valueOf(response.getEncryptCard())));
			result.append(String.format("%47s", " "));
			
		} else {
			throw new RequestInvalidException("Request Invalid : " + data);
		}
		return result.toString();
	}
	
	public static DataModel parseDataField(String info) {
		DataModel response = new DataModel();
		int beginIndex = 34;
		response.setCardNum(info.substring(beginIndex, beginIndex+=20));
		response.setMonthlyInst(info.substring(beginIndex, beginIndex+=2));
		response.setExpDate(info.substring(beginIndex, beginIndex+=4));
		response.setCvc(info.substring(beginIndex, beginIndex+=3));
		response.setBilling(info.substring(beginIndex, beginIndex+=10));
		response.setVAT(info.substring(beginIndex, beginIndex+=10));
		response.setOriginId(info.substring(beginIndex, beginIndex+=20));
		response.setEncryptCard(info.substring(beginIndex, beginIndex+=300));
		return response;
	}
	
	/**
	 * 공통헤더부문 데이터 길이 반환
	 */
	public static int getDataLength() {
		Field[] fs = new PaymentEntity().getClass().getDeclaredFields();
		for (Field f : fs) {
			if (f.isAnnotationPresent(Column.class)) {
				 switch (f.getName()) {
				 case "info" : 
					 return f.getAnnotation(Column.class).length() - 4;
				 }
			}
		}
		return 0;
	}
	
	/**
	 * 관리번호 자동 생성
	 * @throws Exception 
	 */
	public static String idGenerate() throws Exception {
		StringBuffer result = new StringBuffer();
		StringBuffer sb = new StringBuffer();
		Random rand = new Random();
		
		int i = 0;
		while(sb.length() < 20) {
			i += 1;
			sb.append(rand.nextInt(9));
			switch(i) {
				case 5: {
					result.append(typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"5", sb.toString()));
					sb.delete(0,sb.length());
					break;
				}
				case 10: {
					result.append(typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"5", sb.toString()));
					sb.delete(0,sb.length());
					break;
				}
				case 15: {
					result.append(typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"5", sb.toString()));
					sb.delete(0,sb.length());
					break;
				}
				case 20: {
					result.append(typeAlignment(TYPE_NUMERIC_PREFIX_ZERO,
							"5", sb.toString()));
					sb.delete(0,sb.length());
					break;
				}
				default :{
					continue;
				}
			}	
		}
		return result.toString();
	}
	
	/**
	 * 문자열 및 숫자 좌우측정렬처리
	 * @throws Exception 
	 */
	public static String typeAlignment(int type, String digit, String msg)
			throws Exception {
		if (msg.isEmpty())
			throw new RequestInvalidException("Request Invalid : " + msg);
		
		switch(type) {
		case TYPE_NUMERIC_PREFIX_SPACE :
			return String.format("%"+ digit +"d", Integer.parseInt(msg));
		case TYPE_NUMERIC_PREFIX_ZERO :
			return String.format("%0"+ digit +"d", Integer.parseInt(msg));
		case TYPE_NUMERIC_SUFFIX_SPACE :
			return String.format("%-"+ digit +"d", Integer.parseInt(msg));
		case TYPE_STRING_SUFFIX_SPACE :
			return String.format("%-"+ digit +"s", msg);
		default :
			return null;
		}
	}
	
	public static String maskingCardNum(String msg) {
		String beginFrom = msg.substring(0,6);
		String EndTo = msg.substring(msg.length()-3, msg.length());
		int exceptLen = beginFrom.length() + EndTo.length();
		
		String masking = new String(
				new char[msg.length()-exceptLen]).replace("\0", "*");
		
		return beginFrom + masking + EndTo;
	}
}
