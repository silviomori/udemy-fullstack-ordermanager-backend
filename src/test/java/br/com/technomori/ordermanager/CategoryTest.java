package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.assertj.core.api.Fail;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.dto.CategoryDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test(dependsOnGroups="ProductTest")

public class CategoryTest {

	private static Logger log = Logger.getLogger(CategoryTest.class.getName());

	private final String BASE_PATH = TestSuite.SERVER_ADDRESS+"/categories";
	
	private RestTemplate restCategory;
	
	private List<Category> insertedCategories = new ArrayList<Category>();
	
	@BeforeClass
	public void beforeClass() {
		restCategory = new RestTemplate();
	}
	
	@Test( dataProvider = "categoryProvider" )
	public void creatingCategory(Category category) {
		CategoryDTO dto = new CategoryDTO(category);
		URI responseUri = restCategory.postForLocation(BASE_PATH, dto);
		
		assertThat(responseUri).isNotNull();

		Category responseCategory = fetchCategory(responseUri);
		assertThat(responseCategory.getName()).isEqualTo(dto.getName()); 
		
		
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
		CategoryDTO dto = new CategoryDTO(category);
		dto.setName("[U] "+dto.getName());
		URI uri = URI.create(BASE_PATH+"/"+dto.getId());
		restCategory.put(uri, dto);
		
		Category updatedCategory = fetchCategory(uri);
		assertThat(updatedCategory.getId()).isEqualTo(dto.getId());
		assertThat(updatedCategory.getName()).isEqualTo(dto.getName());

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
	
	@Test( dependsOnMethods = "deletingNotAllowed" )
	public void pagingResults() {
		// adding a lot of categories to perform the paging test
		List<URI> addedCategoryUriList = new ArrayList();
		for(int i = 1; i < 51; ++i) {
			URI responseUri = restCategory.postForLocation(
					BASE_PATH,
					Category.builder().name("test"+i).build());
			addedCategoryUriList.add(responseUri);
		}
		
		/*
		 * For now, I am just testing one page.
		 * TODO: Make a test to all pages
		 * TODO: Make tests to all RequestParam
		 */
		//do {
			ResponseEntity<PagedResources<CategoryDTO>> responseEntity =
					restCategory.exchange(BASE_PATH+"/paging",
	                HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<CategoryDTO>>() {});
			
			assertThat(responseEntity).isNotNull();
			assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
			
			Collection<CategoryDTO> collectionDTO = responseEntity.getBody().getContent();
			log.info("Paging categories in database: "+collectionDTO);
		//} while( ** has more pages ** );

		// deleting the categories added to perform this test
		for (URI uri : addedCategoryUriList) {
			restCategory.delete(uri);
		}
	}
	
	@Test
	public void validatingNameOnInsert() {
		Category category = new Category();
		try {
			creatingCategory(category); // must throw an exception
			
			Fail.fail("Category with an empty name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		category.setName("a");
		try {
			creatingCategory(category); // must throw an exception
			
			Fail.fail("Category with a short name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		category.setName(
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890");
		try {
			creatingCategory(category); // must throw an exception

			Fail.fail("Category with a too long name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}		
	}

	@Test
	public void validatingNameOnUpdate() {
		CategoryDTO categoryDTO = CategoryDTO.builder().id(1).name("").build();
		try {
			//Method updatingCategory can not be invoked here because it modifies the category name
			URI uri = URI.create(BASE_PATH+"/"+categoryDTO.getId()); // must throw an exception
			restCategory.put(uri, categoryDTO);

			Fail.fail("Category should not be updated with an empty name.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		categoryDTO.setName("a");
		try {
			//Method updatingCategory can not be invoked here because it modifies the category name
			URI uri = URI.create(BASE_PATH+"/"+categoryDTO.getId()); // must throw an exception
			restCategory.put(uri, categoryDTO);
			
			Fail.fail("Category should not be updated with a short name.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		categoryDTO.setName(
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890"+
				"1234567890");
		try {
			//Method updatingCategory can not be invoked here because it modifies the category name
			URI uri = URI.create(BASE_PATH+"/"+categoryDTO.getId()); // must throw an exception
			restCategory.put(uri, categoryDTO);
			
			Fail.fail("Category should not be updated with a too long name.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}		
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
