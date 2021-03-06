package com.iktpreobuka.elektronskidnevnik.config;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint{
	
	@Override
	public void  afterPropertiesSet() {
		setRealmName("DeveloperStack");
	}
	
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.addHeader("WWW-Authenticate", "Basic realm ="+this.getRealmName());
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		PrintWriter writer  = response.getWriter();
		writer.println("HTTP 401 Authentication failed " + authException.getMessage());
	}
}
