package br.com.technomori.ordermanager.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
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
	@Column(unique=true)
	private String email;
	@JsonIgnore
	private String password;
	private String documentNumber;

	private String pictureProfileUri;
	
	//TODO save ID to database
	private CustomerType customerType;
	
	@ElementCollection
	@CollectionTable(name="PHONE")
	@Singular
	private Set<String> phones;
	
	@OneToMany(mappedBy="customer", cascade=CascadeType.ALL)
	@Builder.Default
	private List<Address> addresses = new ArrayList<Address>();
	
	@JsonIgnore
	@OneToMany(mappedBy="customer")
	@Setter(value=AccessLevel.NONE)
	private List<Order> orders;

	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name="USER_PROFILE")
	@Singular
	private Set<UserProfile> userProfiles;

}
