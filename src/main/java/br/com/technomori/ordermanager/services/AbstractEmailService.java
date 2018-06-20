package br.com.technomori.ordermanager.services;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.Order;

public abstract class AbstractEmailService implements EmailService {

	@Value("${email.default.sender}")
	private String sender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void sendOrderPlacedEmail(Order order) {
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
	
	@Override
	public void sendOrderPlacedHtmlEmail(Order order) {
		MimeMessage mm = null;
		try {
			mm = prepareMimeMailMessage(order);
		} catch (MessagingException e) {
			sendOrderPlacedEmail(order);
		}
		sendHtmlEmail(mm);
	}

	private MimeMessage prepareMimeMailMessage(Order order) throws MessagingException {
		MimeMessage mm = javaMailSender.createMimeMessage();
		
		MimeMessageHelper mmh = new MimeMessageHelper(mm, true);
		mmh.setTo(order.getCustomer().getEmail());
		mmh.setFrom(sender);
		mmh.setSubject("ORDER PLACED: #"+order.getId() );
		mmh.setSentDate(new Date(System.currentTimeMillis()));
		mmh.setText(getHtmlFromOrderPlacedTemplate(order), true);
		
		return mm;
	}

	protected String getHtmlFromOrderPlacedTemplate(Order order) {
		Context ctx = new Context();
		ctx.setVariable("order", order);
		
		return templateEngine.process("email/OrderPlaced", ctx);
	}
	
	@Override
	public void sendNewPasswordEmail(Customer customer, String password) {
		SimpleMailMessage sm = prepareNewPasswordMailMessage(customer, password);
		sendEmail(sm);
	}

	protected SimpleMailMessage prepareNewPasswordMailMessage(Customer customer, String password) {
		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo(customer.getEmail());
		sm.setFrom(sender);
		sm.setSubject("New password requested");
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText("New password: "+password);
		return sm;
	}
}
