package br.com.technomori.ordermanager.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.technomori.ordermanager.dto.CredentialsDTO;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	
	private JWTUtil jwtUtil;

	public JWTAuthenticationFilter( AuthenticationManager authenticationManager, JWTUtil jwtUtil ) {
		setAuthenticationFailureHandler(new JWTAuthenticationFailureHandler());
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Authentication attemptAuthentication(
				HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		
		try {
			CredentialsDTO credentials = new ObjectMapper().readValue(request.getInputStream(), CredentialsDTO.class);
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
					credentials.getEmail(), credentials.getPassword(), new ArrayList<>());
			
			Authentication auth = authenticationManager.authenticate(authToken);
			
			return auth;
		} catch( IOException ex ) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	protected void successfulAuthentication(
				HttpServletRequest request, HttpServletResponse response, 
				FilterChain chain, Authentication authResult) throws IOException, ServletException {
		
		String username = ((UserSpringSecurity) authResult.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(username);
		
		response.addHeader("Authorization", "Bearer "+token);
		response.addHeader("access-control-expose-headers", "Authorization");
	}
	
	
	private class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {

		@Override
		public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException ex) throws IOException, ServletException {

			response.setStatus(401);
			response.setContentType("application/json");
			response.getWriter().append( json() );
		}

		private String json() {
			long timestamp = System.currentTimeMillis();
			return "{\"timestamp\": "+ timestamp +", "
					+ "\"status\": 401, "
					+ "\"error\": \"Unauthorized\", "
					+ "\"message\": \"Authentication Failed: Bad credentials\","
					+ "\"path\": \"/login\"}";
		}
		
	}
}
