package br.com.technomori.ordermanager.domain;

import java.util.Date;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	@JsonFormat(pattern="yyyy/MM/dd")
	private Date expirationDate;

	@JsonFormat(pattern="yyyy/MM/dd")
	private Date paymentDate;

}
