package br.com.technomori.ordermanager;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import br.com.technomori.ordermanager.dto.CredentialsDTO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)

public class RestTemplateFactory {
  
	private static RestTemplate restTemplate;

	static {
		restTemplate = new RestTemplate();

		CredentialsDTO credentials = CredentialsDTO.builder()
				.email("silviomori@gmail.com")
				.password("123")
				.build();
		
		ResponseEntity<Void> responseEntity = restTemplate.postForEntity(TestSuite.SERVER_ADDRESS+"/login", credentials, Void.class);
		List<String> authToken = responseEntity.getHeaders().get("Authorization");

		restTemplate.getInterceptors().add( new AuthorizationInterceptor(authToken) );
        
	}

    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }
}