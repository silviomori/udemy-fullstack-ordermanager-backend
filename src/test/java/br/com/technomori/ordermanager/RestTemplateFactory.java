package br.com.technomori.ordermanager;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.technomori.ordermanager.dto.CredentialsDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)

public class RestTemplateFactory {
  
	private static RestTemplate restTemplateCustomer;
	private static RestTemplate restTemplateAdmin;
	private static RestTemplate restTemplate;

	static {
		// Initializing RestTemplate with Customer profile
		restTemplateCustomer = new RestTemplate();

		CredentialsDTO credentials = CredentialsDTO.builder()
				.email("silviomori@gmail.com")
				.password("123")
				.build();
		
		ResponseEntity<Void> responseEntity = restTemplateCustomer.postForEntity(TestSuite.SERVER_ADDRESS+"/login", credentials, Void.class);
		List<String> authToken = responseEntity.getHeaders().get("Authorization");

		restTemplateCustomer.getInterceptors().add( new AuthorizationInterceptor(authToken) );
        

		// Initializing RestTemplate with Admin profile
		restTemplateAdmin = new RestTemplate();

		credentials = CredentialsDTO.builder()
				.email("technomorisistemas@gmail.com")
				.password("123")
				.build();
		
		responseEntity = restTemplateAdmin.postForEntity(TestSuite.SERVER_ADDRESS+"/login", credentials, Void.class);
		authToken = responseEntity.getHeaders().get("Authorization");

		restTemplateAdmin.getInterceptors().add( new AuthorizationInterceptor(authToken) );

		// Initializing RestTemplate with No profile
		restTemplate = new RestTemplate();
	}

    public static RestTemplate getRestTemplateCustomerProfile() {
        return restTemplateCustomer;
    }

    public static RestTemplate getRestTemplateAdminProfile() {
        return restTemplateAdmin;
    }

    public static RestTemplate getRestTemplateNoProfile() {
        return restTemplate;
    }
}