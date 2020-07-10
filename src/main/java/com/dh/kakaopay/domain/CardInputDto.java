package com.dh.kakaopay.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CardInputDto {

	@Size(min = 10, max = 16, message = "카드번호는 10~16자리 사이입니다.")
	@Pattern(regexp="[0-9]+",message="카드번호 형식은 숫자입니다.")
	private String cardNo;

	@Size(min = 4, max = 4, message = "유효기간은 4자리 입니다.")
	@Pattern(regexp="[0-9]+",message="유효기간 형식은 숫자입니다.")
	private String exprYm;

	@Size(min = 3, max = 3, message = "CVC는 3자리 입니다.")
	@Pattern(regexp="[0-9]+",message="CVC 형식은 숫자입니다.")
	private String cvc;

	@PositiveOrZero
	@Max(12)
	private int loanYmd;
	
	@Min(100)
	@Max(1000000000)
	private long payAmt;
	
	private long vatAmt;

}
