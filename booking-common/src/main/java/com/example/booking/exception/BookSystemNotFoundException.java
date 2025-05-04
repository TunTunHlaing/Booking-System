package com.example.booking.exception;

import lombok.Getter;

@Getter
public class BookSystemNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private String message;

	public BookSystemNotFoundException(String message) {
		super();
		this.message = message;
	}
	
	

}
