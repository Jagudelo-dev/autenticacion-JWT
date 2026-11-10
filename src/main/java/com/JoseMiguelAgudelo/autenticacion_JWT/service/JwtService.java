package com.JoseMiguelAgudelo.autenticacion_JWT.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${env.jwt.secret-key}")
	private String secretKey;

	@Value("${env.jwt.token-expiration}")
	private long tokenExpiration;

	private SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Long userId, String username, Long rolId) {
		Date issuedAt = new Date();
		Date expiration = new Date(issuedAt.getTime() + tokenExpiration);

		return Jwts.builder()
				.claim("userId", userId)
				.claim("rolId", rolId)
				.subject(username)
				.issuedAt(issuedAt)
				.expiration(expiration)
				.signWith(getSignKey())
				.compact();
	}

	public boolean isTokenValid(String token) {
		try {
			Jwts.parser()
					.verifyWith(getSignKey())
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (JwtException exception) {
			return false;
		}
	}

	private <T> T extractClaims(String token, Function<Claims, T> resolver) {
		Claims claims = Jwts.parser()
				.verifyWith(getSignKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return resolver.apply(claims);
	}

	public String extractSubject(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public Long extractUserId(String token) {
		return extractClaims(token, claims -> extractLongClaim(claims, "userId"));
	}

	public Long extractRolId(String token) {
		return extractClaims(token, claims -> extractLongClaim(claims, "rolId"));
	}

	public String refreshToken(String token) {
		try {
			Claims claims = Jwts.parser()
					.verifyWith(getSignKey())
					.build()
					.parseSignedClaims(token)
					.getPayload();
			String username = claims.getSubject();
			Long userId = extractLongClaim(claims, "userId");
			Long rolId = extractLongClaim(claims, "rolId");
			return generateToken(userId, username, rolId);
		} catch (ExpiredJwtException exception) {
			throw new IllegalArgumentException("Token is expired", exception);
		} catch (JwtException exception) {
			throw new IllegalArgumentException("Token is invalid", exception);
		} catch (Exception exception) {
			throw new IllegalStateException("Internal server error", exception);
		}
	}

	private Long extractLongClaim(Claims claims, String claimName) {
		Object value = claims.get(claimName);
		if (value instanceof Number number) {
			return number.longValue();
		}
		throw new IllegalArgumentException("JWT claim '" + claimName + "' must be numeric");
	}
}
