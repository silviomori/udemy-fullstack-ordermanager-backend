package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
//@DiscriminatorValue("CREDIT_CARD")

@Configuration

public class CreditCardPayment extends Payment {

	public static final String IDENTIFIER = "CreditCardPayment";
	
	private Integer installments;

	@Bean(IDENTIFIER)
	public Payment newPayment() {
		return new CreditCardPayment();
	}

}
