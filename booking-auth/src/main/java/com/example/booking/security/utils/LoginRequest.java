package com.example.booking.security.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
		@NotBlank(message = "Email Not Be Blank!")
		@NotNull(message = "Email Not Be Null!")
		String email,
		@NotBlank(message = "Password Not Be Blank!")
		@NotNull(message = "Password Not Be Null!")
		String password) {

}
