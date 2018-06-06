package br.com.technomori.ordermanager.dto;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import br.com.technomori.ordermanager.domain.Customer;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class CustomerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotEmpty(message="Required field.")
	@Length(min=5,max=120, message="Length must be between 5 and 80.")
	private String name;
	@NotEmpty(message="Required field.")
	@Email(message="Invalid email address.")
	private String email;
	
	public CustomerDTO(Customer customer) {
		id = customer.getId();
		name = customer.getName();
		email = customer.getEmail();
	}

}
