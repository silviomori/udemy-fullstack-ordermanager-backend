package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

import br.com.technomori.ordermanager.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
//TODO Change strategy to InheritanceType.JOINED
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
//@DiscriminatorColumn(name="TYPE", discriminatorType=DiscriminatorType.STRING)
//@DiscriminatorValue("null")
public abstract class Payment {

	@Id
	private Integer id;
	
	//TODO save ID to database
	private PaymentStatus paymentStatus;

	@JsonBackReference
	@OneToOne
	@MapsId
	private Order order;

}
