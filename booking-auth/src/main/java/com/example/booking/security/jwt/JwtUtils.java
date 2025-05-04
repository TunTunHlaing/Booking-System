package com.example.booking.security.jwt;

import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.booking.exception.BookingSystemException;
import com.example.booking.security.utils.TokenResponse;
import com.example.booking.security.utils.TokenType;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtils {

	@Value("${app.jwt.token.secret}")
	private String SECRET_KEY;
	@Value("${app.jwt.token.issuer}")
	private String issuer;
    private SecretKey key;

    private static final String TOKEN_TYPE = "tokenType";
    private static final String AUTH = "authorities";


    @PostConstruct
    private void init() {
        this.key = getKey(SECRET_KEY);
    }
    
    public String generateToken(String username , TokenType type) {
    	var current = Calendar.getInstance();
		var builder = Jwts.builder().signWith(key);

		builder.subject(username)
		.issuer(issuer)
		.issuedAt(current.getTime())
		.expiration(getExpiration(type, current))
		.claim(TOKEN_TYPE, type.toString())
		.claim(AUTH, username);
		
		return builder.compact();
    }

    private SecretKey getKey(String key) {
        return new SecretKeySpec(Base64.getDecoder().decode(key), "HmacSHA512");
    }
    
    public TokenResponse parseToken(String token) {
    	
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new BookingSystemException("Token is null or empty");
            }

            String tokenValue = token.trim().startsWith("Bearer ") ? token.trim().substring(7).trim() : token.trim();

            JwtParser parser = Jwts.parser()
                    .requireIssuer(issuer)
                    .verifyWith(key)
                    .build();

            Jws<Claims> jws = parser.parseSignedClaims(tokenValue);
            Claims claims = jws.getPayload();
            
            var username = claims.getSubject();
			var tokenType = claims.get(TOKEN_TYPE, String.class);
			var authorities = claims.get(AUTH, String.class) != null ? Arrays.asList(claims.get(AUTH, String.class).split(",")) : null;
			
			return new TokenResponse(username, TokenType.valueOf(tokenType),
					 authorities);		
			
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token Expired");
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid Token: " + e.getMessage());
        }
    }

    private Date getExpiration(TokenType type, Calendar current) {

        switch (type) {
            case ACCESS:
                current.add(Calendar.MINUTE, 5); 
                break;

            case REFRESH:
                current.add(Calendar.MINUTE, 30);
                break;

            default:
                throw new IllegalArgumentException("Invalid token type: " + type.toString());
        }

        return current.getTime();
    }
}
    