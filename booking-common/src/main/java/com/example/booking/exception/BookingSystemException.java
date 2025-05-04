package com.example.booking.exception;

import lombok.Getter;

@Getter
public class BookingSystemException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private String message;
	public BookingSystemException(String message) {
		super();
		this.message = message;
	}
	
	
}
