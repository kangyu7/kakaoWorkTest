package com.dh.kakaopay.error;

import org.springframework.http.HttpStatus;

public class KakaopayException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	private String message;
	
	public KakaopayException(String message) {
		this.status = HttpStatus.BAD_REQUEST;
		this.message = message;
	}
	
	public KakaopayException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
	
	public KakaopayException(Exception ex) {
		this.message = ex.getMessage();
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String toString() {
		return "status : " + status +
			   " message : " + message;
	}
	
}
