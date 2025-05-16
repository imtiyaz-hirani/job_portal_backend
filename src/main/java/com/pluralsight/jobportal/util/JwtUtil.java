package com.pluralsight.jobportal.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
	// Replace with a strong Key: Save it in Environment file during production
	private static final String SECRET_KEY = "MY_SECRET_KEY_123456789012345678901234567890"; 
																								// key
	private static final long EXPIRATION_TIME = 86400000; // 1 day (in milliseconds)

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	//Generate JWT Token for loggedIn user
	public String generateToken(String email) {
		return Jwts.builder()
				.setSubject(email).setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	//Extract Email from JWT Token
	public String extractEmail(String token) {
		return Jwts.parserBuilder()
					.setSigningKey(getSigningKey())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
	}

	//Validate JWT Token
	public boolean validateToken(String token, String email) {
		return extractEmail(token).equals(email);
	}
}
