package com.example.booking.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.booking.entity.BookingUser;
import com.example.booking.exception.BookSystemNotFoundException;
import com.example.booking.exception.BookingSystemException;
import com.example.booking.repo.BookingUserRepo;
import com.example.booking.security.jwt.JwtUtils;
import com.example.booking.security.utils.LoginRequest;
import com.example.booking.security.utils.LoginResponse;
import com.example.booking.security.utils.TokenType;
import com.example.booking.utils.ErrorMessageConstant;

@Service
public class AuthenticationService {

	@Autowired
	private BookingUserRepo bookingUserRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtUtils jwtUtils;


	public LoginResponse login (LoginRequest request) {
		var user = bookingUserRepo.findByEmail(request.email())
				.orElseThrow(() -> new BookSystemNotFoundException(ErrorMessageConstant.NOTFOUND_001));
		
		validateLoginUser(request, user);
		return new LoginResponse(
				jwtUtils.generateToken(user.getEmail(), TokenType.ACCESS),
				jwtUtils.generateToken(user.getEmail(), TokenType.REFRESH));
	}
	
	private void validateLoginUser(LoginRequest request, BookingUser user) {
		if (!passwordEncoder.matches(request.password(), user.getPassword())) {
			throw new BookingSystemException(ErrorMessageConstant.PASSWORD_ERROR_001);

		}
	}
	
	public LoginResponse refreshToken(String token) {

		var tokenResult = jwtUtils.parseToken(token);
		if (tokenResult.type() != TokenType.REFRESH) {
			throw new BookingSystemException("You Can't Refresh With Access Token!");
		}
		var authentication = UsernamePasswordAuthenticationToken.authenticated(tokenResult.username(), null,
				tokenResult.authorities().stream().map(a -> new SimpleGrantedAuthority(a)).toList());

		if (null == authentication || !authentication.isAuthenticated()) {
			throw new BookingSystemException(ErrorMessageConstant.AUTH_ERROR_001);
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		return new LoginResponse(
				jwtUtils.generateToken(tokenResult.username(), TokenType.ACCESS),
				jwtUtils.generateToken(tokenResult.username(), TokenType.REFRESH));

	}
}
