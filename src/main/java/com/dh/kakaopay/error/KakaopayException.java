package com.dh.kakaopay.error;

import org.springframework.http.HttpStatus;

public class KakaopayException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
	private String className;
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

	public KakaopayException(String className, Exception ex) {
		this.className = className;
		this.message = ex.getMessage();
	}
	
	public KakaopayException(String className, String message) {
		this.status = HttpStatus.BAD_REQUEST;
		this.className = className;
		this.message = message;
	}

	public KakaopayException(HttpStatus status, String className, String message) {
		this.status = status;
		this.className = className;
		this.message = message;
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
		return "status : " + status.toString() +
			   " className : " + className +   
			   " message : " + message;
	}
	
	public String getClassName() {
		return className;
	}
}
