package br.com.technomori.ordermanager.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service

public class AuthService {

	@Autowired
	private BCryptPasswordEncoder passEncoder;
	
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private EmailService emailService;
	
	private Random random = new Random();
	
	public void sendNewPassword( String email ) {
		Customer customer = customerRepository.findByEmail(email);
		if( customer == null ) {
			throw new ObjectNotFoundException("Email not found: "+email);
		}
		
		String newPass = generateNewPassword();
		
		customer.setPassword( passEncoder.encode(newPass) );
		customerRepository.save(customer);
		
		emailService.sendNewPasswordEmail(customer, newPass);
	}

	private String generateNewPassword() {
		int passSize = 10;
		char[] newPass = new char[passSize];
		for( int i = 0; i < passSize; i++ ) {
			newPass[i] = randomChar();
		}
		return new String(newPass);
	}

	private char randomChar() {
		int opt = random.nextInt(3);
		switch( opt ) {
			case 0: // generate a digit
				return (char) (random.nextInt(10) + 48);
			case 1: // generate a uppercase letter
				return (char) (random.nextInt(26) + 65);
			case 2: // generate a lowercase letter
				return (char) (random.nextInt(26) + 97);
		}
		return 0;
	}

}
