package br.com.technomori.ordermanager.domain;

import javax.persistence.Entity;

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
public class CreditCardPayment extends Payment {

	private Integer installments;

}
