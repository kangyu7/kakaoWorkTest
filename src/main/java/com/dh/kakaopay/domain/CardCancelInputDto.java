package com.dh.kakaopay.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CardCancelInputDto {

	private String inspNo;
	
	@Min(100)
	@Max(1000000000)
	private long cancelAmt;
	
	private String cancelVatAmt;

}
