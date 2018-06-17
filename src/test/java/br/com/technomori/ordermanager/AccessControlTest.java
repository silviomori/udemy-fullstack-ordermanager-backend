package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

import org.assertj.core.api.Fail;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.dto.CredentialsDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test(groups = "AccessControlTest")

public class AccessControlTest {

	private static Logger log = Logger.getLogger(AccessControlTest.class.getName());

	private final String BASE_PATH = TestSuite.SERVER_ADDRESS+"/login";

	private RestTemplate restLogin;

	@BeforeClass
	public void beforeClass() {
		restLogin = new RestTemplate();
	}

	public void loginBadCredentials() {
		
		CredentialsDTO credentialsDTO = CredentialsDTO.builder()
				.email("nulo")
				.password("nulo")
				.build();
		
		try {
			restLogin.postForEntity(BASE_PATH, credentialsDTO, Void.class);
			
			Fail.fail("Invalid credentials should not be authorized.");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		}
	}

	public void loginSuccessful() {
		CredentialsDTO credentialsDTO = CredentialsDTO.builder()
				.email("silviomori@gmail.com")
				.password("123")
				.build();
		
		ResponseEntity<Void> responseEntity = restLogin.postForEntity(BASE_PATH, credentialsDTO, Void.class);

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	public void publicAccess() {
		ResponseEntity<?> responseEntity = restLogin.getForEntity(TestSuite.SERVER_ADDRESS+"/h2-console", null, Object.class);

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	public void publicGetAccess() {
		ResponseEntity<Category> responseEntity = restLogin.getForEntity(TestSuite.SERVER_ADDRESS+"/categories/1", null, Category.class);

		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);		
	}
	
	public void forbiddenAccess() {
		try {
			restLogin.postForEntity(
					TestSuite.SERVER_ADDRESS+"/categories", Category.builder().name("new category").build(), Object.class);
		
			Fail.fail("A new category must not be created by a unauthorized user.");
		} catch( HttpClientErrorException e ) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
	}
	
	public void accessAuthorized() throws URISyntaxException {
		CredentialsDTO credentialsDTO = CredentialsDTO.builder()
				.email("silviomori@gmail.com")
				.password("123")
				.build();
		
		ResponseEntity<Void> responseAuthorization = restLogin.postForEntity(BASE_PATH, credentialsDTO, Void.class);
		assertThat(responseAuthorization).isNotNull();
		assertThat(responseAuthorization.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<String> auth = responseAuthorization.getHeaders().get("Authorization");
		assertThat(auth)
			.isNotNull()
			.isNotEmpty();
		
		RequestEntity<Category> requestEntity = RequestEntity
				.post(new URI(TestSuite.SERVER_ADDRESS+"/categories"))
				.header("Authorization", auth.toArray(new String[auth.size()]))
				.body(Category.builder().name("new category").build());

		ResponseEntity<?> responseCategory = restLogin.exchange(requestEntity, Object.class);

		assertThat(responseCategory).isNotNull();
		assertThat(responseCategory.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	public void invalidToken() throws URISyntaxException {
		CredentialsDTO credentialsDTO = CredentialsDTO.builder()
				.email("silviomori@gmail.com")
				.password("123")
				.build();
		
		ResponseEntity<Void> responseAuthorization = restLogin.postForEntity(BASE_PATH, credentialsDTO, Void.class);
		assertThat(responseAuthorization).isNotNull();
		assertThat(responseAuthorization.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<String> auth = responseAuthorization.getHeaders().get("Authorization");
		assertThat(auth)
			.isNotNull()
			.isNotEmpty();
		
		String[] authArray = auth.toArray(new String[auth.size()]);
		authArray[0] = authArray[0].replace(authArray[0].charAt(8), (char)(authArray[0].charAt(8)+1));

		RequestEntity<Category> requestEntity = RequestEntity
				.post(new URI(TestSuite.SERVER_ADDRESS+"/categories"))
				.header("Authorization", authArray )
				.body(Category.builder().name("new category").build());

		try {
			restLogin.exchange(requestEntity, Object.class);

			Fail.fail("An invalid token should not be validated.");
		} catch( HttpClientErrorException e ) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
	}
	
}
