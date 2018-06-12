package br.com.technomori.ordermanager.services;

import java.util.logging.Logger;

import org.springframework.mail.SimpleMailMessage;

public class MockEmailService extends AbstractEmailService {

	private static final Logger LOG = Logger.getLogger(MockEmailService.class.getName());
	
	@Override
	public void sendEmail(SimpleMailMessage msg) {
		LOG.info( "Email sending simulation...");
		LOG.info(msg.toString());
		LOG.info("Email has been sent!");
	}

}
