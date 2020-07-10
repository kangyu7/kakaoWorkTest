package com.dh.kakaopay.domain;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CardSearchDto {

	@NotBlank(message = "inspNo는 필수 입력 값입니다.")
	private String inspNo;
}
