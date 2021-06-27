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

필수구현 API : 완성,
선택구현 API : 미완성
