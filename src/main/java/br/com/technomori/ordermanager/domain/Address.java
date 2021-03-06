package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class Address {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	@NotEmpty(message="Required field.")
	private String street;
	@NotEmpty(message="Required field.")
	private String number;
	private String complement;
	private String district;
	@NotEmpty(message="Required field.")
	private String zipCode;
	
	@ManyToOne
	@NotNull(message="Required field.")
	private City city;

	@JsonIgnore
	@ManyToOne
	private Customer customer;
}
