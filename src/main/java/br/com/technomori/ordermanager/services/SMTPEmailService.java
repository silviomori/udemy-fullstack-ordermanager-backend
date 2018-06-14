package br.com.technomori.ordermanager.services;

import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SMTPEmailService extends AbstractEmailService {

	private static final Logger LOG = Logger.getLogger(MockEmailService.class.getName());

	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void sendEmail(SimpleMailMessage msg) {
		LOG.info( "Email sending...");
		mailSender.send(msg);
		LOG.info("Email has been sent!");
	}

	@Override
	public void sendHtmlEmail(MimeMessage msg) {
		LOG.info( "HTML email sending...");
		javaMailSender.send(msg);
		LOG.info("HTML email has been sent!");
	}

}
