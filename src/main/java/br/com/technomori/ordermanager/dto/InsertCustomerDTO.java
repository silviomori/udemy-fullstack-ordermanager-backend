package br.com.technomori.ordermanager.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
import br.com.technomori.ordermanager.services.validation.CustomerInsert;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
@CustomerInsert
public class InsertCustomerDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@NotEmpty(message="Required field.")
	@Length(min=5,max=120, message="Length must be between 5 and 80.")
	private String name;
	@NotEmpty(message="Required field.")
	@Email(message="Invalid email address.")
	private String email;
	@NotEmpty
	private String password;
	@NotEmpty(message="Required field.")
	private String documentNumber;
	private CustomerType customerType;

	@Singular
	@NotEmpty
	private List<InsertAddressDTO> addresses;
	
	@NotEmpty
	@Singular
	private Set<String> phoneNumbers;
	
	@Singular
	private Set<UserProfile> userProfiles = new HashSet<UserProfile>();
}
