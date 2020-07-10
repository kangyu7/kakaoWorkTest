package com.dh.kakaopay.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "LOCK_DATA")
public class LockData implements Serializable {
	private static final long serialVersionUID = 8124227299932339862L;
	
	@Id
	@Column(name = "id")
	private String id;

}
