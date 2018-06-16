package br.com.technomori.ordermanager.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.security.UserSpringSecurity;

@Service

public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	CustomerRepository customerRepo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Customer customer = customerRepo.findByEmail(email);
		if(customer == null) {
			throw new UsernameNotFoundException(email);
		}
		
		UserSpringSecurity userSS = UserSpringSecurity.builder()
				.id(customer.getId())
				.email(customer.getEmail())
				.password(customer.getPassword())
				.build();
		userSS.setUserProfiles(customer.getUserProfiles());
		
		return userSS;
		
	}

}
