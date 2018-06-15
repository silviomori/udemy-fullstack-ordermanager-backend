package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.dto.InsertAddressDTO;
import br.com.technomori.ordermanager.dto.InsertCustomerDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test(dependsOnGroups="ProductTest")

public class CustomerTest {

	private static Logger log = Logger.getLogger(CustomerTest.class.getName());

	private final String BASE_PATH = TestSuite.SERVER_ADDRESS+"/customers";

	private RestTemplate restCustomer;

	private List<Customer> insertedCustomers = new ArrayList<Customer>();

	@BeforeClass
	public void beforeClass() {
		restCustomer = new RestTemplate();
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

		URI responseUri = restCustomer.postForLocation(BASE_PATH, dto);

		assertThat(responseUri).isNotNull();

		Customer responseCustomer = fetchCustomer(responseUri);

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
	
		return responseUri;
	}

	@Test(dependsOnMethods = "creatingCustomerTest")
	public void fetchAll() {
		ResponseEntity<List> responseEntity = restCustomer.getForEntity(BASE_PATH, List.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().size()).isGreaterThanOrEqualTo(insertedCustomers.size());

		log.info("Reading all customers in database: " + responseEntity.getBody());
	}

	@Test(dependsOnMethods = "fetchAll", dataProvider = "customerProvider")
	public void updatingCustomer(Customer customer) {
		URI uri = URI.create(BASE_PATH + "/" + customer.getId());
		customer = fetchCustomer(uri);
		customer.setName("[U] " + customer.getName());
		customer.setEmail("u" + customer.getEmail());

		CustomerDTO dto = new CustomerDTO(customer);
		restCustomer.put(uri, dto);

		Customer updatedCustomer = fetchCustomer(uri);
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
			restCustomer.delete(uri);
		} catch (HttpClientErrorException ex) { // Customer was not found to be deleted
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			log.info("Deleting customer: Customer is no longer in database: " + customer);
			return;
		}

		try { // try to find the deleted customer in database
			restCustomer.getForEntity(uri, Customer.class);
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
			restCustomer.delete(uri);
		} catch (HttpClientErrorException ex) { // Deletion has not been allowed
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

		Customer customerRemainsInDatabase = fetchCustomer(customerId);
		log.info("Deleting customer: Not allowed to delete customer: " + customerRemainsInDatabase);
	}

	@Test(dependsOnMethods = "deletingNotAllowed")
	public void pagingResults() {
		 // adding a lot of customers to perform the paging test
		 List<URI> addedCustomerUriList = new ArrayList();
		 for(int i = 1; i < 51; ++i) {
			 URI responseUri = restCustomer.postForLocation(
				 BASE_PATH,
				 getGenericCustomerToInsert(i));
			 addedCustomerUriList.add(responseUri);
		 }

		/*
		 * For now, I am just testing one page.
		 * TODO: Make a test to all pages
		 * TODO: Make tests to all RequestParam
		 */
		//do {
			ResponseEntity<PagedResources<CustomerDTO>> responseEntity = restCustomer.exchange(BASE_PATH + "/paging",
					HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<CustomerDTO>>() {
					});
	
			assertThat(responseEntity).isNotNull();
			assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
	
			Collection<CustomerDTO> collectionDTO = responseEntity.getBody().getContent();
			log.info("Paging customers in database: " + collectionDTO);
		//} while( ** has more pages ** );

		 // deleting the customers added to perform this test
		 for (URI uri : addedCustomerUriList) {
			 restCustomer.delete(uri);
		 }
	}
	
	@Test
	public void validatingNameOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setName("");
		
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with an empty name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}

		dto.setName("a");
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with a short name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingNameOnUpdate() {
		CustomerDTO customerDTO = CustomerDTO.builder().id(1).name("").email("email@email.com").build();
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH + "/" + customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer should not be updated with an empty name.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}

		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer should not be updated with a short name.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer should not be updated with a too long name.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setEmail("");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with an empty email address should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}

		dto.setEmail("a");
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with a invalid email address format should not be inserted in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailUniquenessOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1000);
		
		// Customer must be created successfully
		creatingCustomer(dto);
		
		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with non unique email address should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
	}

	@Test
	public void validatingEmailOnUpdate() {
		CustomerDTO customerDTO = CustomerDTO.builder().id(1).name("Aaaaaaaaaa Bbbbbbbbb").email("").build();
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer with an empty email address should not be updated in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}

		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer with a invalid email address format should not be updated in database.");
		} catch ( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
	}
	
	@Test
	public void validatingEmailUniquenessOnUpdate() {
		int pin = 2000;
		InsertCustomerDTO dto = getGenericCustomerToInsert(pin);

		// Customer must be created successfully
		URI responseURI = creatingCustomer(dto);
		
		Customer customer = fetchCustomer(responseURI);
		String updatedName = "[U] " + customer.getName();
		String uniqueEmail = customer.getEmail();
		
		customer.setName(updatedName);
		// !! DO NOT change email
		restCustomer.put(responseURI, customer);

		// Verifying that customer was really updated
		customer = fetchCustomer(responseURI);
		assertThat(customer.getName()).isEqualTo(updatedName);

		// Inserting a new customer, with all different data
		dto = getGenericCustomerToInsert(++pin);
		responseURI = creatingCustomer(dto);

		// Updating the new customer with an already used email
		customer = fetchCustomer(responseURI);
		customer.setEmail(uniqueEmail);
		
		try {
			restCustomer.put(responseURI, customer);
			Fail.fail("Different customers should not have the same email");
		} catch( HttpClientErrorException e ) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info("Exception caught as expected. "+e.getMessage());
		}
	}

	@Test
	public void validatingCPFOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setCustomerType(CustomerType.INDIVIDUAL);
		dto.setDocumentNumber("112233");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with no valid CPF should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingCNPJOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setCustomerType(CustomerType.CORPORATE);
		dto.setDocumentNumber("112233");

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with no valid CNPJ should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingAddressOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setAddresses(new ArrayList());
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with empty address list should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}
		
		dto = getGenericCustomerToInsert(1);
		dto.getAddresses().get(0).setStreet("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Address with empty street name should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert(1);
		dto.getAddresses().get(0).setNumber("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Address with no number should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert(1);
		dto.getAddresses().get(0).setZipCode("");
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with empty zip code should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}

		dto = getGenericCustomerToInsert(1);
		dto.getAddresses().get(0).setCityId(null);
		try {
			creatingCustomer(dto); // must throw an exception
			Fail.fail("Customer with no city id defined should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}

	}

	@Test
	public void validatingPhoneNotNullOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setPhoneNumbers(new HashSet());

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with empty phone set should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}
	}

	@Test
	public void validatingCustomerTypeNotNullOnInsert() {
		InsertCustomerDTO dto = getGenericCustomerToInsert(1);
		dto.setCustomerType(null);

		try {
			creatingCustomer(dto); // must throw an exception

			Fail.fail("Customer with empty customer type should not be inserted in database.");
		} catch (HttpClientErrorException ex) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
			return;
		}
	}


	private Customer fetchCustomer(Integer customerId) {
		URI uri = URI.create(BASE_PATH+"/"+customerId);
		return fetchCustomer(uri);
	}

	private Customer fetchCustomer(URI uri) {
		ResponseEntity<Customer> responseEntity = restCustomer.getForEntity(uri, Customer.class);
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);

		Customer responseCustomer = responseEntity.getBody();
		assertThat(responseCustomer).isNotNull();

		return responseCustomer;
	}
	
	private InsertCustomerDTO getGenericCustomerToInsert(int pin) {
		InsertAddressDTO address = InsertAddressDTO.builder()
				.street("5th Avenue"+pin)
				.number("5009"+pin)
				.complement("ap 92"+pin)
				.district("Alphaville"+pin)
				.zipCode("11223-001"+pin)
				.cityId(1)
				.build();

		InsertCustomerDTO customer = InsertCustomerDTO.builder()
				.name("Silvio Mori Neto"+pin)
				.email("silviomori@gmail.com"+pin)
				.documentNumber("11122233344")
				.customerType(CustomerType.INDIVIDUAL)
				.address(address)
				.phoneNumber("11-99890-9988"+pin)
				.phoneNumber("11-99890-9989"+pin)
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

}
