package com.example.booking.exception;

import lombok.Getter;

@Getter
public class ConcurrentTryException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	private String message;

	public ConcurrentTryException(String message) {
		super();
		this.message = message;
	}
	
	

}
