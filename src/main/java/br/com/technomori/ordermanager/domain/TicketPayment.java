package br.com.technomori.ordermanager.domain;

import java.util.Date;

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
//@DiscriminatorValue("TICKET")
public class TicketPayment extends Payment {
	
	private Date expirationDate;
	private Date paymentDate;

}
