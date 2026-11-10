package com.JoseMiguelAgudelo.autenticacion_JWT.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.JoseMiguelAgudelo.autenticacion_JWT.service.JwtService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtValidationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
			writeUnauthorized(response, "Header Authorization is missing in the request");
			return;
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length());
		try {
			if (!jwtService.isTokenValid(token)) {
				writeUnauthorized(response, "Token is invalid");
				return;
			}

			request.setAttribute("username", jwtService.extractSubject(token));
			request.setAttribute("userId", jwtService.extractUserId(token));
			request.setAttribute("rolId", jwtService.extractRolId(token));
		} catch (JwtException exception) {
			writeUnauthorized(response, "Token is invalid");
			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String pathWithinApplication = request.getRequestURI().substring(contextPath.length());
		return pathWithinApplication.startsWith("/auth/")
				|| pathWithinApplication.startsWith("/api/v1/auth/");
	}

	private void writeUnauthorized(HttpServletResponse response, String error) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"error\":\"" + error + "\"}");
	}
}
