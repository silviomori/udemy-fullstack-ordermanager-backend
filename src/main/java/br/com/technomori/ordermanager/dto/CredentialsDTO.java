package br.com.technomori.ordermanager.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder

public class CredentialsDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	
}
