package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Data;

@Entity
@Data
@Builder
public class City {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String name;
	
	@ManyToOne
	private State state;

}
