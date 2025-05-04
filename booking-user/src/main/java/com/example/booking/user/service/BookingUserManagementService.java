package com.example.booking.user.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.booking.entity.BookingUser;
import com.example.booking.exception.BookSystemNotFoundException;
import com.example.booking.exception.BookingSystemException;
import com.example.booking.repo.BookingUserRepo;
import com.example.booking.user.model.input.BookUserCreateRequest;
import com.example.booking.user.model.input.PasswordChangeRequest;
import com.example.booking.user.model.input.ResetPasswordRequest;
import com.example.booking.user.model.output.BookUserResponse;
import com.example.booking.utils.ErrorMessageConstant;

@Service
public class BookingUserManagementService {

	@Autowired
	private BookingUserRepo userRepo;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public BookUserResponse createUser(BookUserCreateRequest createRequest) {
		var user = createRequest.toEntity();
		user.setPassword(passwordEncoder.encode(createRequest.password()));
		var entity = userRepo.save(user);
		return BookUserResponse.fromEntity(entity);
	}
	
	public BookUserResponse getUserProfile(Long userId) {
		var user = userRepo.findById(userId)
				.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
		return BookUserResponse.fromEntity(user);
	}
	
	
	public String chagePasswrod(
			Principal principal,
			PasswordChangeRequest changeReuqest) {
		
		if (!changeReuqest.newPassword().equals(changeReuqest.confirmPassword())) {
			throw new BookingSystemException("Password Not Match!");
		}
		
		BookingUser bookUser = userRepo.findByEmail(principal.getName())
				.orElseThrow(() -> new BookingSystemException(ErrorMessageConstant.NOTFOUND_001));
		
		if (!passwordEncoder.matches(changeReuqest.oldPassword(), bookUser.getPassword())) {
			throw new BookingSystemException(ErrorMessageConstant.PASSWORD_ERROR_001);
		}
		
		bookUser.setPassword(passwordEncoder.encode(changeReuqest.newPassword()));
		userRepo.save(bookUser);
		return "Password Changed!";
		
	}
	
	
	public String resetPassword(
			ResetPasswordRequest request
			) {
		if (!request.newPassword().equals(request.confirmPassword())) {
			throw new BookingSystemException("Password Not Match!");
		}
		BookingUser bookUser = userRepo.findByEmail(request.email())
				.orElseThrow(() -> new BookingSystemException(ErrorMessageConstant.NOTFOUND_001));
		bookUser.setPassword(passwordEncoder.encode(request.newPassword()));
		userRepo.save(bookUser);
		return "Password Changed!";
	}
	
}
