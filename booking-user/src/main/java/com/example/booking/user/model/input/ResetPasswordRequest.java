package com.example.booking.user.model.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest(
		@NotNull(message = "Email Can't Be Null!")
		@NotBlank(message = "Email Can't Be Blank!")
		@Email
		String email,
		@NotNull(message = "New Password Can't Be Null!")
		@NotBlank(message = "New Password Can't Be Blank!")
		String newPassword,
		@NotBlank(message = "Confirm Password Can't Be Blank!")
		@NotNull(message = "Confirm Password Can't Be Null!")
		String confirmPassword) {

}
