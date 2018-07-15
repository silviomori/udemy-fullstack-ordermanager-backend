package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.assertj.core.api.Fail;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
import br.com.technomori.ordermanager.dto.CredentialsDTO;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.dto.InsertAddressDTO;
import br.com.technomori.ordermanager.dto.InsertCustomerDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test(dependsOnGroups="ProductTest")

public class CustomerTest {

	private static Logger log = Logger.getLogger(CustomerTest.class.getName());
	private static int PIN = 0;

	private final String BASE_PATH = TestSuite.SERVER_ADDRESS+"/customers";

	private List<Customer> insertedCustomers = new ArrayList<Customer>();

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@BeforeClass
	public void beforeClass() {
	}

	// TODO: Make a test with a Customer Type null
	@Test(dataProvider = "insertCustomerProvider")
	public void creatingCustomerTest(InsertCustomerDTO dto) {
		creatingCustomer(dto);
	}
	
	@Test(dataProvider = "insertCustomerProvider")
	public void creatingCustomerWithNoUserProfileTest(InsertCustomerDTO dto) {
		dto.setEmail(dto.getEmail()+"tesUserProfile");
		dto.setUserProfiles(new HashSet<UserProfile>());
		creatingCustomer(dto);
	}
	
	public URI creatingCustomer(InsertCustomerDTO dto) {
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplate();

		URI uriResponse = restTemplate.postForLocation(BASE_PATH, dto);

		assertThat(uriResponse).isNotNull();

		doLogin(dto, restTemplate);
		
		Customer responseCustomer = fetchCustomer(uriResponse, restTemplate);

		assertThat(responseCustomer).isNotNull();
		assertThat(responseCustomer.getCustomerType()).isEqualTo(dto.getCustomerType());
		assertThat(responseCustomer.getDocumentNumber()).isEqualTo(dto.getDocumentNumber());
		assertThat(responseCustomer.getEmail()).isEqualTo(dto.getEmail());
		assertThat(responseCustomer.getName()).isEqualTo(dto.getName());
		assertThat(responseCustomer.getPhones().size()).isEqualTo(dto.getPhoneNumbers().size());
		assertThat(responseCustomer.getAddresses().size()).isEqualTo(dto.getAddresses().size());
		
		if(dto.getUserProfiles().size() == 0) {
			assertThat(responseCustomer.getUserProfiles().size()).isEqualTo(dto.getUserProfiles().size()+1);
		} else {
			assertThat(responseCustomer.getUserProfiles().size()).isEqualTo(dto.getUserProfiles().size());
		}

		assertThat(insertedCustomers.add(responseCustomer)).isTrue();

		log.info("Created customer: " + responseCustomer);
	
		return uriResponse;
	}

	private void doLogin(InsertCustomerDTO dto, RestTemplate restTemplate) {
		CredentialsDTO credentials = CredentialsDTO.builder()
				.email(dto.getEmail())
				.password(dto.getPassword())
				.build();
		restTemplate.postForEntity(TestSuite.SERVER_ADDRESS+"/login", credentials, Void.class);
	}

	@Test
	private void fetchCustomerByEmail() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		
		creatingCustomer(dto);
		
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplate();
		doLogin(dto, restTemplate);
		
		String uri = BASE_PATH+"/email?value="+dto.getEmail();
		ResponseEntity<Customer> responseEntity = restTemplate.getForEntity(uri, Customer.class);
		
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

		Customer responseCustomer = responseEntity.getBody();
		assertThat(responseCustomer).isNotNull();
		assertThat(responseCustomer.getEmail()).isEqualTo(dto.getEmail());
	}
	
	@Test(dependsOnMethods = "creatingCustomerTest")
	public void fetchAll() {
		ResponseEntity<List> responseEntity = RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(BASE_PATH, List.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().size()).isGreaterThanOrEqualTo(insertedCustomers.size());

		log.info("Reading all customers in database: " + responseEntity.getBody());
	}

	@Test(dependsOnMethods = "fetchAll", dataProvider = "customerProvider")
	public void updatingCustomer(Customer customer) {
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplateAdminProfile();
		URI uri = URI.create(BASE_PATH + "/" + customer.getId());
		customer = fetchCustomer(uri,restTemplate);
		customer.setName("[U] " + customer.getName());
		customer.setEmail("u" + customer.getEmail());

		CustomerDTO dto = new CustomerDTO(customer);
		restTemplate.put(uri, dto);

		Customer updatedCustomer = fetchCustomer(uri,restTemplate);
		assertThat(updatedCustomer).isEqualToComparingOnlyGivenFields(customer, "id", "name", "email",
				"documentNumber");

		log.info("Updated customer: " + updatedCustomer);
	}

	/*
	 * Invoking this method twice: - first: to delete customers created in database
	 * - second: to try to delete customers that are no longer in database
	 */
	@Test(dependsOnMethods = "updatingCustomer", groups="deletingCustomer", dataProvider = "customerProvider", invocationCount = 2)
	public void deletingCustomer(Customer customer) {
		URI uri = URI.create(BASE_PATH + "/" + customer.getId());

		try { // try to delete the customer from database
			RestTemplateFactory.getRestTemplateAdminProfile().delete(uri);
		} catch (HttpClientErrorException ex) { // Customer was not found to be deleted
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			log.info("Deleting customer: Customer is no longer in database: " + customer);
			return;
		}

		try { // try to find the deleted customer in database
			RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(uri, Customer.class);
		} catch (HttpClientErrorException ex) { // Customer has just been deleted
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			log.info("Deleted customer: " + customer);
			insertedCustomers.remove(customer);
			return;
		}

		Fail.fail("Deletion has not worked properly.");
	}

	
	@Test(dependsOnMethods = "deletingCustomer")
	public void deletingNotAllowed() {
		int customerId = 1;
		URI uri = URI.create(BASE_PATH + "/" + customerId);
		try { // try to delete the customer from database
			RestTemplateFactory.getRestTemplateAdminProfile().delete(uri);
		} catch (HttpClientErrorException ex) { // Deletion has not been allowed
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

		Customer customerRemainsInDatabase = fetchCustomer(customerId);
		log.info("Deleting customer: Not allowed to delete customer: " + customerRemainsInDatabase);
	}

	@Test(dependsOnMethods = "deletingNotAllowed")
	public void pagingResults() {
		 // adding a lot of customers to perform the paging test
		 List<URI> addedCustomerUriList = new ArrayList<URI>();
		 for(int i = 1; i < 10; ++i) {
			 URI responseUri = RestTemplateFactory.getRestTemplateNoProfile().postForLocation(
				 BASE_PATH,
				 getGenericCustomerToInsert());
			 addedCustomerUriList.add(responseUri);
		 }

		/*
		 * For now, I am just testing one page.
		 * TODO: Make a test to all pages
		 * TODO: Make tests to all RequestParam
		 */
		//do {
			ResponseEntity<PagedResources<CustomerDTO>> responseEntity = RestTemplateFactory.getRestTemplateAdminProfile().exchange(BASE_PATH + "/paging?pageNumber=2&linesPerPage=5",
					HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<CustomerDTO>>() {
					});
	
			assertThat(responseEntity).isNotNull();
			assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
	
			Collection<CustomerDTO> collectionDTO = responseEntity.getBody().getContent();
			log.info("Paging customers in database: " + collectionDTO);
		//} while( ** has more pages ** );

		 // deleting the customers added to perform this test
		 for (URI uri : addedCustomerUriList) {
			 RestTemplateFactory.getRestTemplateAdminProfile().delete(uri);
		 }
	}
	
	@Test
	public void validatingNameOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setName("");
		
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with an empty name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		dto.setName("a");
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with a short name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		dto.setName(
				"1234567890"+
				"1234567890"+
				"1234567890"+
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
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with a too long name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingNameOnUpdate() {
		CustomerDTO customerDTO = CustomerDTO.builder().id(1).name("").email("email@email.com").build();
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH + "/" + customerDTO.getId()); // must throw an exception
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uri, customerDTO);

			Fail.fail("Customer should not be updated with an empty name.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uri, customerDTO);

			Fail.fail("Customer should not be updated with a short name.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		customerDTO.setName(
				"1234567890"+
				"1234567890"+
				"1234567890"+
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
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uri, customerDTO);

			Fail.fail("Customer should not be updated with a too long name.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setEmail("");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with an empty email address should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		dto.setEmail("a");
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with a invalid email address format should not be inserted in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailUniquenessOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		
		// Customer must be created successfully
		creatingCustomer(dto);
		
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with non unique email address should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailOnUpdate() {
		CustomerDTO customerDTO = CustomerDTO.builder().id(1).name("Aaaaaaaaaa Bbbbbbbbb").email("").build();
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uri, customerDTO);

			Fail.fail("Customer with an empty email address should not be updated in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}

		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uri, customerDTO);

			Fail.fail("Customer with a invalid email address format should not be updated in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
		}
	}
	
	@Test
	public void validatingEmailUniquenessOnUpdate() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();

		// Customer must be created successfully
		URI responseURI = creatingCustomer(dto);

		
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplate();
		doLogin(dto, restTemplate);
		
		Customer customer = fetchCustomer(responseURI, restTemplate);
		String updatedName = "[U] " + customer.getName();
		String uniqueEmail = customer.getEmail();
		
		customer.setName(updatedName);
		// !! DO NOT change email
		restTemplate.put(responseURI, customer);

		// Verifying that customer was really updated
		customer = fetchCustomer(responseURI, restTemplate);
		assertThat(customer.getName()).isEqualTo(updatedName);

		// Inserting a new customer, with all different data
		dto = getGenericCustomerToInsert();
		responseURI = creatingCustomer(dto);

		// Updating the new customer with an already used email
		doLogin(dto, restTemplate);
		customer = fetchCustomer(responseURI, restTemplate);
		customer.setEmail(uniqueEmail);
		
		try {
			restTemplate.put(responseURI, customer);
			Fail.fail("Different customers should not have the same email");
		} catch( HttpClientErrorException e ) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info("Exception caught as expected. "+e.getMessage());
		}
	}

	@Test
	public void validatingCPFOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setCustomerType(CustomerType.INDIVIDUAL);
		dto.setDocumentNumber("112233");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with no valid CPF should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingCNPJOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setCustomerType(CustomerType.CORPORATE);
		dto.setDocumentNumber("112233");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with no valid CNPJ should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingAddressOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setAddresses(new ArrayList<InsertAddressDTO>());
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with empty address list should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}
		
		dto = getGenericCustomerToInsert();
		dto.getAddresses().get(0).setStreet("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Address with empty street name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert();
		dto.getAddresses().get(0).setNumber("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Address with no number should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert();
		dto.getAddresses().get(0).setZipCode("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with empty zip code should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert();
		dto.getAddresses().get(0).setCityId(null);
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with no city id defined should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}

	}

	@Test
	public void validatingPhoneNotNullOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setPhoneNumbers(new HashSet<String>());

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with empty phone set should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingCustomerTypeNotNullOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert();
		dto.setCustomerType(null);

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with empty customer type should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
			log.info(ex.getMessage());
			return;
		}
	}
	
	@Test
	public void testingAccessControlToEndpoints() {
		
		// GET - by ID
		try {
			RestTemplateFactory.getRestTemplateNoProfile().getForEntity(BASE_PATH+"/1", Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		RestTemplateFactory.getRestTemplateCustomerProfile().getForEntity(BASE_PATH+"/1", Object.class);
		RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(BASE_PATH+"/1", Object.class);

		// GET - All
		try {
			RestTemplateFactory.getRestTemplateNoProfile().getForEntity(BASE_PATH, Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().getForEntity(BASE_PATH, Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(BASE_PATH, Object.class);

		// GET - Paging
		try {
			RestTemplateFactory.getRestTemplateNoProfile().getForEntity(BASE_PATH+"/paging", Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().getForEntity(BASE_PATH+"/paging", Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(BASE_PATH+"/paging", Object.class);


		// POST
		InsertCustomerDTO insertCustomerDTO_1 = getGenericCustomerToInsert();
		URI uriInsertedCustomer_1 = RestTemplateFactory.getRestTemplateNoProfile().postForLocation(BASE_PATH, insertCustomerDTO_1);
		InsertCustomerDTO insertCustomerDTO_2 = getGenericCustomerToInsert();
		URI uriInsertedCustomer_2 = RestTemplateFactory.getRestTemplateCustomerProfile().postForLocation(BASE_PATH, insertCustomerDTO_2);
		InsertCustomerDTO insertCustomerDTO_3 = getGenericCustomerToInsert();
		URI uriInsertedCustomer_3 = RestTemplateFactory.getRestTemplateAdminProfile().postForLocation(BASE_PATH, insertCustomerDTO_3);
		
		
		// PUT
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplate();
		doLogin(insertCustomerDTO_3, restTemplate);

		Customer insertedCustomer_3 = restTemplate
				.getForEntity(uriInsertedCustomer_3, Customer.class)
				.getBody();
		CustomerDTO customerDTO = new CustomerDTO(insertedCustomer_3);
		customerDTO.setName(insertedCustomer_3.getName()+" - updated");
		
		try {
			RestTemplateFactory.getRestTemplateNoProfile().put(uriInsertedCustomer_3, customerDTO);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		
		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().put(uriInsertedCustomer_3, customerDTO);

			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		
		restTemplate.put(uriInsertedCustomer_3, customerDTO);

		customerDTO.setName(insertedCustomer_3.getName()+" - updated 2");
		RestTemplateFactory.getRestTemplateAdminProfile().put(uriInsertedCustomer_3, customerDTO);

		
		// DELETE
		try {
			RestTemplateFactory.getRestTemplateNoProfile().delete(uriInsertedCustomer_3);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		
		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().delete(uriInsertedCustomer_3);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		
		try {
			restTemplate.delete(uriInsertedCustomer_3);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}		
		
		RestTemplateFactory.getRestTemplateAdminProfile().delete(uriInsertedCustomer_1);
		RestTemplateFactory.getRestTemplateAdminProfile().delete(uriInsertedCustomer_2);
		RestTemplateFactory.getRestTemplateAdminProfile().delete(uriInsertedCustomer_3);
	}

	@Test
	public void fetchingDistinctUser_Forbidden() {
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplateCustomerProfile();
		try {
			fetchCustomer(URI.create(BASE_PATH+"/2"), restTemplate);
			
			Fail.fail("Customer should not be allowed to fetch a distinct user.");
		} catch( HttpClientErrorException e ) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
	}

	@Test
	public void fetchingSameUser() {
		RestTemplate restTemplate = RestTemplateFactory.getRestTemplateCustomerProfile();
		fetchCustomer(URI.create(BASE_PATH+"/1"), restTemplate);
	}

	@Test
	public void fetchingDistinctUser_byAdminUser() {
		fetchCustomer(URI.create(BASE_PATH+"/1"), RestTemplateFactory.getRestTemplateAdminProfile());
	}


	@Test(dataProvider = "profilePictureProvider")
	public void savingProfilePicture(HttpEntity<LinkedMultiValueMap<String, ClassPathResource>> requestEntity) throws IOException {
		URI pictureUri = RestTemplateFactory.getRestTemplateCustomerProfile().postForLocation(BASE_PATH+"/picture", requestEntity);
		assertThat(pictureUri).isNotNull();

		Resource resource = resourceLoader.getResource(pictureUri.toString());
		assertThat(resource).isNotNull();

		InputStream inputStream = resource.getInputStream();
		assertThat(inputStream).isNotNull();

		BufferedImage buffImg = ImageIO.read( inputStream );
		assertThat(buffImg).isNotNull();
		assertThat(buffImg.getHeight()).isEqualTo(200);
		assertThat(buffImg.getWidth()).isEqualTo(200);
	}

	private Customer fetchCustomer(Integer customerId) {
		URI uri = URI.create(BASE_PATH+"/"+customerId);
		return fetchCustomer(uri,RestTemplateFactory.getRestTemplateAdminProfile());
	}

	private Customer fetchCustomer(URI uri, RestTemplate restTemplate) {
		ResponseEntity<Customer> responseEntity = restTemplate.getForEntity(uri, Customer.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

		Customer responseCustomer = responseEntity.getBody();
		assertThat(responseCustomer).isNotNull();

		return responseCustomer;
	}
	
	private InsertCustomerDTO getGenericCustomerToInsert() {
		PIN++; // increment the PIN value to generate a unique customer.
		InsertAddressDTO address = InsertAddressDTO.builder()
				.street("5th Avenue"+PIN)
				.number("5009"+PIN)
				.complement("ap 92"+PIN)
				.district("Alphaville"+PIN)
				.zipCode("11223-001"+PIN)
				.cityId(1)
				.build();

		InsertCustomerDTO customer = InsertCustomerDTO.builder()
				.name("Silvio Mori Neto"+PIN)
				.email("silviomori@gmail.com"+PIN)
				.documentNumber("11122233344")
				.customerType(CustomerType.INDIVIDUAL)
				.address(address)
				.phoneNumber("11-99890-9988"+PIN)
				.phoneNumber("11-99890-9989"+PIN)
				.password("123")
				.build();

		return customer;
	}
	
	@DataProvider
	private Customer[] customerProvider() {
		return insertedCustomers.toArray(new Customer[insertedCustomers.size()]);
	}

	@DataProvider
	private InsertCustomerDTO[] insertCustomerProvider() {
		InsertAddressDTO address = InsertAddressDTO.builder()
				.street("5th Avenue")
				.number("5009")
				.complement("ap 92")
				.district("Alphaville")
				.zipCode("11223-001")
				.cityId(1)
				.build();

		InsertCustomerDTO customer = InsertCustomerDTO.builder()
				.name("Silvio Mori Neto")
				.email("silviomori_test@gmail.com")
				.documentNumber("11122233344")
				.customerType(CustomerType.INDIVIDUAL)
				.address(address)
				.phoneNumber("11-99890-9988")
				.phoneNumber("11-99890-9989")
				.password("123")
				.userProfiles(Arrays.asList(UserProfile.values()))
				.build();

		return new InsertCustomerDTO[] { customer };
	}
	
	@DataProvider
	private HttpEntity<LinkedMultiValueMap<String, ClassPathResource>>[] profilePictureProvider() {
		return new HttpEntity[] {
				// imagem JPG larga com altura > 200
				getHttpEntity("238x212.jpg"),
				
				// imagem JPG alta com largura > 200
				getHttpEntity("240x320.jpg"),
				
				// imagem JPG larga com altura < 200
				getHttpEntity("282x179.jpg"),
				
				// imagem JPG alta com largura < 200
				getHttpEntity("183x275.jpg"),
				
				// imagem JPG quadrada > 200 x 200
				getHttpEntity("100x100.jpg"),
				
				// imagem JPG quadrada < 200 x 200
				getHttpEntity("225x225.jpg"),
				
				// imagem JPG quadrada == 200 x 200
				getHttpEntity("200x200.jpg"),
				
				// imagem PNG
				getHttpEntity("700x720.png"),

				// imagem com mais de 1 mb
				getHttpEntity("1mb.png"),		
		};
	}

	private HttpEntity<LinkedMultiValueMap<String, ClassPathResource>> getHttpEntity(String fileName) {
		//InputStream is = getClass().getClassLoader().getResourceAsStream("profile_pictures/"+fileName);
		
		
		LinkedMultiValueMap<String, ClassPathResource> map = new LinkedMultiValueMap<String, ClassPathResource>();
		map.add("file", new ClassPathResource("./profile_pictures/"+fileName));
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		HttpEntity<LinkedMultiValueMap<String, ClassPathResource>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, ClassPathResource>>(map, headers);
				
		
		return requestEntity;
	}

}

