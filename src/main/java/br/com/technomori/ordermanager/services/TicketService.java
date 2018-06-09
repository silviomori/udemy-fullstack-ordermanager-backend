package br.com.technomori.ordermanager.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.TicketPayment;

@Service

public class TicketService {

	public void fill(TicketPayment ticket, Date orderDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(orderDate);
		calendar.add(Calendar.DAY_OF_MONTH, 7);
		
		ticket.setExpirationDate(calendar.getTime());
	}
}
