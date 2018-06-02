package br.com.technomori.ordermanager.domain;

import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import br.com.technomori.ordermanager.domain.enums.ClientType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class Customer {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private String email;
	private String documentNumber;

	//TODO save ID to database
	private ClientType clientType;
	
	@JsonManagedReference
	@ElementCollection
	@CollectionTable(name="PHONE")
	@Singular
	private Set<String> phones;
	
	@JsonManagedReference
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	@Setter(value=AccessLevel.NONE)
	private List<Address> addresses;
	
	@JsonBackReference
	@OneToMany(mappedBy="customer", orphanRemoval=true)
	@Setter(value=AccessLevel.NONE)
	private List<Order> orders;
}
