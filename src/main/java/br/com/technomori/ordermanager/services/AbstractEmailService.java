package br.com.technomori.ordermanager.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import br.com.technomori.ordermanager.domain.Order;

public abstract class AbstractEmailService implements EmailService {

	@Value("${email.default.sender}")
	private String sender;
	
	@Override
	public void sendEmail(Order order) {
		SimpleMailMessage sm = prepareSimpleMailMessage(order);
		sendEmail(sm);
	}

	protected SimpleMailMessage prepareSimpleMailMessage(Order order) {
		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo(order.getCustomer().getEmail());
		sm.setFrom(sender);
		sm.setSubject("ORDER PLACED: #"+order.getId() );
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText(order.toString());
		return sm;
	}
}
