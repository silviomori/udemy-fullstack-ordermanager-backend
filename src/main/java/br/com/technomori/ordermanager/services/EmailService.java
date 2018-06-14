package br.com.technomori.ordermanager.services;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;

import br.com.technomori.ordermanager.domain.Order;

public interface EmailService {

	void sendOrderPlacedEmail(Order order);

	void sendOrderPlacedHtmlEmail(Order order);

	void sendEmail(SimpleMailMessage msg);
	
	void sendHtmlEmail(MimeMessage msg);
}
