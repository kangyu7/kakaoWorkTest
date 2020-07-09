package com.dh.kakaopay.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "CARD_DATA")
public class CardData implements Serializable {
	private static final long serialVersionUID = 8124227299932339862L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "insp_no", length = 20)
	private String inspNo;
	
	@Column(name = "card_String", length = 450)
	private String cardString;
}
