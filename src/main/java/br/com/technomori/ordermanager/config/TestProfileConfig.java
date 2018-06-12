package br.com.technomori.ordermanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.technomori.ordermanager.services.DBService;
import br.com.technomori.ordermanager.services.EmailService;
import br.com.technomori.ordermanager.services.MockEmailService;

@Configuration
@Profile("test")

public class TestProfileConfig {

	@Autowired
	private DBService dbService;
	
	@Bean
	public boolean instatiateDatabase() {
		return dbService.instantiateDatabase();
	}

	@Bean
	public EmailService emailService() {
		return new MockEmailService();
	}
}
