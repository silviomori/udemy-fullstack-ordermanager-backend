package br.com.technomori.ordermanager.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class EmailDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message="Required field.")
	@Email(message="Invalid email address.")
	private String email;
}
