package com.dh.kakaopay.domain;

import lombok.Data;

@Data
public class CardInputData  {

	private String inspNo;
	private String cardNo;
	private String exprYm;
	private String cvc;
	private int loanYmd;
	private long payAmt;
	private long cancelAmt;
	private long vatAmt;
	private long cancelVatAmt;

}
