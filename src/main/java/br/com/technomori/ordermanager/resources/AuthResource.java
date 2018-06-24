package br.com.technomori.ordermanager.resources;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.technomori.ordermanager.dto.EmailDTO;
import br.com.technomori.ordermanager.security.JWTUtil;
import br.com.technomori.ordermanager.security.UserSpringSecurity;
import br.com.technomori.ordermanager.services.AuthService;
import br.com.technomori.ordermanager.services.UserService;

@RestController
@RequestMapping(value = "/auth")

public class AuthResource {

	@Autowired
	private JWTUtil jwtUtil;
	
	@Autowired
	private AuthService authService;
	
	@RequestMapping(value = "/refresh_token", method = RequestMethod.POST)
	public ResponseEntity<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		UserSpringSecurity user = UserService.authenticated();
		String token = jwtUtil.generateToken(user.getUsername());
		
		response.addHeader("Authorization", "Bearer "+token);
		response.addHeader("access-control-expose-headers", "Authorization");
		
		return ResponseEntity.ok().build();
	}

	@RequestMapping(value = "/forgotten", method = RequestMethod.POST)
	public ResponseEntity<Void> refreshToken( @Valid @RequestBody EmailDTO emailDTO ) {
		authService.sendNewPassword(emailDTO.getEmail());
		return ResponseEntity.ok().build();
	}

}
