package br.com.technomori.ordermanager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import br.com.technomori.ordermanager.services.DBService;
import br.com.technomori.ordermanager.services.EmailService;
import br.com.technomori.ordermanager.services.SMTPEmailService;

@Configuration
@Profile("dev")

public class DevProfileConfig {

	@Autowired
	private DBService dbService;
	
	@Value("${spring.jpa.hibernate.ddl-auto}")
	private String databaseStrategy;
	
	@Bean
	public boolean instatiateDatabase() {
		if( databaseStrategy.contains("create") ) {
			return dbService.instantiateDatabase();			
		}
		return true;
	}
	
	@Bean
	public EmailService emailService() {
		return new SMTPEmailService();
	}

}
