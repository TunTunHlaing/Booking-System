package com.example.booking.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.booking.security.utils.TokenType;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	@Value("${app.jwt.token.secret}")
	private String secretKey;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		var token = request.getHeader("authorization");

		if (token != null && token.startsWith("Bearer ")) {

			token = token.substring(7);
			try {
				var tokenResponse = jwtUtils.parseToken(token);
				if (tokenResponse.type() != TokenType.ACCESS) {
					throw new RuntimeException("Can't use refresh token!");
				}
				
				String username = tokenResponse.username();
				if (username != null) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null,
							new ArrayList<>());
					SecurityContextHolder.getContext().setAuthentication(auth);

				}

			} catch (Exception e) {
				e.printStackTrace();
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

}