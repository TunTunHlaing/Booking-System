package com.example.booking.user.model.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PasswordChangeRequest(
		@NotNull(message = "Old Password Can't Be Null!") @NotBlank(message = "Old Password Can't Be Blank!") 
		String oldPassword,
		@NotNull(message = "New Password Can't Be Null!") @NotBlank(message = "New Password Can't Be Blank!") 
		String newPassword,
		@NotNull(message = "Confirm Password Can't Be Null!") @NotBlank(message = "Confirm Password Can't Be Blank!") 
		String confirmPassword) {

}
