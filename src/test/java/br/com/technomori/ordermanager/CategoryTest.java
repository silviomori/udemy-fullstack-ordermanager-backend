package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
	
	private List<Category> insertedCategories = new ArrayList<Category>();
	
	@BeforeClass
	public void beforeClass() {
		restCategory = new RestTemplate();
	}
	
	@Test( dataProvider = "categoryProvider" )
	public void creatingCategory(Category category) {
		URI responseUri = restCategory.postForLocation(BASE_PATH, category);
		
		assertThat(responseUri).isNotNull();

		Category responseCategory = fetchCategory(responseUri);
		assertThat(responseCategory).isEqualToIgnoringGivenFields(category, 
				"id","products");
		
		insertedCategories.add(responseCategory);
	}
	
	@Test( dependsOnMethods = "creatingCategory", dataProvider = "categoryProvider" )
	public void updatingCategory(Category category) {
		category.setName("[U] "+category.getName());
		URI uri = URI.create(BASE_PATH+"/"+category.getId());
		restCategory.put(uri, category);
		
		Category updatedCategory = fetchCategory(uri);
		assertThat(updatedCategory).isEqualToIgnoringGivenFields(category,
				"products");
	}


	private Category fetchCategory(Integer categoryId) {
		URI uri = URI.create(BASE_PATH+"/"+categoryId);
		return fetchCategory(uri);
	}
	
	private Category fetchCategory(URI uri) {
		ResponseEntity<Category> responseEntity = restCategory.getForEntity(uri, Category.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		
		Category responseCategory = responseEntity.getBody();
		assertThat(responseCategory).isNotNull();
		
		return responseCategory;
	}

	@DataProvider
	private Category[] categoryProvider() {
		if( !insertedCategories.isEmpty() ) {
			return insertedCategories.toArray(new Category[insertedCategories.size()]);
		}

		return new Category[] {
				Category.builder().name("Furniture").build()
			};
	}
}
