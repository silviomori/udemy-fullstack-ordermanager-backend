package br.com.technomori.ordermanager.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.technomori.ordermanager.domain.Order;

public interface EmailService {

	void sendEmail(Order order);
	
	void sendEmail(SimpleMailMessage msg);
}
