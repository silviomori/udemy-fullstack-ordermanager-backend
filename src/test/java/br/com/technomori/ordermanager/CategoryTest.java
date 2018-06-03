package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.logging.Logger;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.com.technomori.ordermanager.domain.Category;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test
public class CategoryTest {

	private static Logger log = Logger.getLogger(CategoryTest.class.getName());

	private final String BASE_PATH = "http://localhost:8080/categories";
	
	private RestTemplate restCategory;
	
	@BeforeClass
	public void beforeClass() {
		restCategory = new RestTemplate();
	}
	
	@Test( dataProvider = "categoryProvider" )
	public void creatingCategory(Category category) {
		URI responseUri = restCategory.postForLocation(BASE_PATH, category);
		
		assertThat(responseUri).isNotNull();

		ResponseEntity<Category> responseEntity = restCategory.getForEntity(responseUri, Category.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isEqualToIgnoringGivenFields(category, 
				"id","products");
	}
	
	@DataProvider
	private Category[] categoryProvider() {
		return new Category[] {
			Category.builder().name("Furniture").build()
		};
	}
}
