package com.example.booking.user.api;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.booking.user.model.input.PasswordChangeRequest;
import com.example.booking.user.model.output.BookUserResponse;
import com.example.booking.user.service.BookingUserManagementService;
import com.example.booking.utils.ApiResponse;

@RestController
@RequestMapping("user-management")
public class BookingUserManagementApi {
	
	@Autowired
	private BookingUserManagementService managementService;
	
	@GetMapping("{userId}")
	public ApiResponse<BookUserResponse> getProfile(@PathVariable("userId") Long userId) {
		var res = managementService.getUserProfile(userId);
		return ApiResponse.createSuccessResponse(res, "Successfully fetch user profile.");

	}
	
	@PutMapping("change-pwd")
	public ApiResponse<String> changePassword(Principal principal,
			@Validated @RequestBody PasswordChangeRequest changeRequest) {
		return ApiResponse.createSuccessResponse(managementService.chagePasswrod(principal, changeRequest), "Successfully Changed Password!");

	}
}
