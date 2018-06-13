package br.com.technomori.ordermanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.technomori.ordermanager.services.EmailService;
import br.com.technomori.ordermanager.services.SMTPEmailService;

@Configuration
@Profile("prod")

public class ProdProfileConfig {

	@Bean
	public EmailService emailService() {
		return new SMTPEmailService();
	}

}
