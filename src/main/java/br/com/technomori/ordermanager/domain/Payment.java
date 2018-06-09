package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@paymentType" )
@JsonSubTypes( {
	@JsonSubTypes.Type(value= TicketPayment.class, name = TicketPayment.IDENTIFIER),
	@JsonSubTypes.Type(value= CreditCardPayment.class, name = CreditCardPayment.IDENTIFIER)
})

@Configuration

public abstract class Payment {

	@Id
	private Integer id;
	
	//TODO save ID to database
	private PaymentStatus paymentStatus;

	@JsonIgnore
	@OneToOne
	@MapsId
	private Order order;

	@Bean
	public abstract Payment newPayment();
}
