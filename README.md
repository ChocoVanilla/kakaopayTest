# kakaopayTest
카카오페이 코딩테스트 리포지토리

과제1.결제요청을 받아 카드사와 통신하는 인터페이스를 제공하는 결제시스템

테이블 정의
DROP TABLE PAYMENT;

CREATE TABLE PAYMENT (
    PAYMENT_ID VARCHAR(20), 
    PAYMENT_INFO VARCHAR(450) NOT NULL,
    CONSTRAINT PAYMENT_ID_KEY PRIMARY KEY(PAYMENT_ID)
);

기본적인 동작![img](https://user-images.githubusercontent.com/48255013/123556147-859aab00-d7c4-11eb-8f19-1a235fa0ee3d.png)
예외처리에서 ExceptionHandler는 시간관계상 작성하지 않음
입력검정은 400 Bad Request로 출력

필수구현 API : 완성, 선택구현 API : 미완성
1. 결제 API : POST방식
2. 취소 API : POST방식
3. 조회/전제조회 API : GET방식
4. 카드정보 암호화 방식 : AES
- 암호화는 결제 시 호출하여 DB에 저장하지만, 복호화는 구현 후 사용개소가 없음. 테스트 정상출력 확인.


