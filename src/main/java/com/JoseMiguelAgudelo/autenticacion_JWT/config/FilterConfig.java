package com.JoseMiguelAgudelo.autenticacion_JWT.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.JoseMiguelAgudelo.autenticacion_JWT.filter.JwtValidationFilter;

@Configuration
public class FilterConfig {

	@Bean
	FilterRegistrationBean<JwtValidationFilter> jwtValidationFilterRegistration(
			JwtValidationFilter jwtValidationFilter) {
		FilterRegistrationBean<JwtValidationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(jwtValidationFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}
}
