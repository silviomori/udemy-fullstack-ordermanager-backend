package br.com.technomori.ordermanager.services;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SMTPEmailService extends AbstractEmailService {

	private static final Logger LOG = Logger.getLogger(MockEmailService.class.getName());

	@Autowired
	private MailSender mailSender;
	
	@Override
	public void sendEmail(SimpleMailMessage msg) {
		LOG.info( "Email sending...");
		mailSender.send(msg);
		LOG.info("Email has been sent!");
	}

}
