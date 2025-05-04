package com.example.booking.user.model.input;

import com.example.booking.entity.BookingUser;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BookUserCreateRequest(
		@NotNull(message = "Name Can't Be Null!")
		@NotBlank(message = "Name Can't Be Blank!")
		String name,
		@NotNull(message = "Email Can't Be Null!")
		@NotBlank(message = "Name Can't Be Blank!")
		@Email
		String email,
		@NotNull(message = "Password Can't Be Null!")
		@NotBlank(message = "Name Can't Be Blank!")
		String password,
		String address) {
	

	public BookingUser toEntity() {
		var user = new BookingUser();
		user.setName(name);
		user.setEmail(email);
		user.setAddress(address);
		user.setActive(true);
		return user;
	}
}
