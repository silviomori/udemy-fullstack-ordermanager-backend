package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.assertj.core.api.Fail;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
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
		
		log.info("Created category: "+responseCategory);
	}
	
	@Test( dependsOnMethods = "creatingCategory")
	public void fetchAll() {
		ResponseEntity<List> responseEntity = restCategory.getForEntity(BASE_PATH, List.class);	
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).hasAtLeastOneElementOfType(Object.class);
		
		log.info("Reading all categories in database: "+responseEntity.getBody());
	}

	@Test( dependsOnMethods = "fetchAll", dataProvider = "categoryProvider" )
	public void updatingCategory(Category category) {
		category.setName("[U] "+category.getName());
		URI uri = URI.create(BASE_PATH+"/"+category.getId());
		restCategory.put(uri, category);
		
		Category updatedCategory = fetchCategory(uri);
		assertThat(updatedCategory).isEqualToIgnoringGivenFields(category,
				"products");

		log.info("Updated category: "+updatedCategory);
	}

	/*
	 * Invoking this method twice:
	 * 	- first:	to delete categories created in database
	 *  - second:	to try to delete categories that are no longer in database
	 */
	@Test( dependsOnMethods = "updatingCategory", dataProvider = "categoryProvider", invocationCount=2 )
	public void deletingCategory(Category category) {
		URI uri = URI.create(BASE_PATH+"/"+category.getId());
		
		try { // try to delete the category from database
			restCategory.delete(uri);
		} catch(HttpClientErrorException ex) { // Category was not found to be deleted
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			log.info("Deleting category: Category is no longer in database: "+category);
			return;
		}

		try { // try to find the deleted category in database
			restCategory.getForEntity(uri, Category.class);
		} catch(HttpClientErrorException ex) { // Category has just been deleted
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			log.info("Deleted category: "+category);
			return;
		}
		
		Fail.fail("Deletion has not worked properly.");

	}

	@Test( dependsOnMethods = "deletingCategory" )
	public void deletingNotAllowed() {
		int categoryId = 1;
		URI uri = URI.create(BASE_PATH+"/"+categoryId);
		try { // try to delete the category from database
			restCategory.delete(uri);
		} catch(HttpClientErrorException ex) { // Deletion has not been allowed
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

		Category categoryRemainsInDatabase = fetchCategory(categoryId);
		log.info("Deleting category: Not allowed to delete category: "+categoryRemainsInDatabase);
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
