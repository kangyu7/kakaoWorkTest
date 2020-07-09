package com.dh.kakaopay.error;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dh.kakaopay.KakaopayTestApplication;

@RestControllerAdvice
public class KakaopayControllerAdvice {
	
	protected static final Logger logger = LoggerFactory.getLogger(KakaopayTestApplication.class);

	@ExceptionHandler(value = {KakaopayException.class})
	public KakaopayError KakaopayException(KakaopayException re, HttpServletResponse response){
		
		response.setStatus(re.getStatus().value());
		KakaopayError rbsError = new KakaopayError();
		rbsError.setStatus(re.getStatus().toString());
		rbsError.setClassName(re.getClassName());
		rbsError.setMessage(re.getMessage());
		return rbsError;
	}
	
	@ExceptionHandler(value = {Exception.class})
	public KakaopayError Exception(Exception ex, HttpServletResponse response){
		
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		KakaopayError rbsError = new KakaopayError();
		rbsError.setClassName(ex.getClass().getName());
		rbsError.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		rbsError.setMessage(ex.getMessage());
		return rbsError;
	}

}
