package com.springboot.restful.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payment")
public class PaymentEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "payment_id")
	private String id;
	
	@Column(name = "payment_info", length = 450)
	private String info;
	
}	