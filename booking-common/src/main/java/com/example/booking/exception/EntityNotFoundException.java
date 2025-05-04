package com.example.booking.exception;

import lombok.Data;

@Data
public class EntityNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	private String message;

	public EntityNotFoundException(String message) {
		super();
		this.message = message;
	}
	
	

}
