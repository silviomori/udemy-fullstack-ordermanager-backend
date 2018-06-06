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

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.dto.CustomerDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test
public class CustomerTest {

	private static Logger log = Logger.getLogger(CustomerTest.class.getName());

	private final String BASE_PATH = "http://localhost:8080/customers";

	private RestTemplate restCustomer;
	
	private List<Customer> insertedCustomers = new ArrayList<Customer>();
	
	@BeforeClass
	public void beforeClass() {
		restCustomer = new RestTemplate();
	}

	// Method to insert a new customer not implemented yet
	@Test( dataProvider = "customerProvider", enabled = false)
	public void creatingCustomer(Customer customer) {
		CustomerDTO dto = new CustomerDTO(customer);

		URI responseUri = restCustomer.postForLocation(BASE_PATH, dto);
		
		assertThat(responseUri).isNotNull();

		Customer responseCustomer = fetchCustomer(responseUri);
		assertThat(responseCustomer.getName()).isEqualTo(dto.getName()); 
		
		insertedCustomers.add(responseCustomer);
		
		log.info("Created customer: "+responseCustomer);
	}
	
	// Method to insert a new customer not implemented yet
	@Test//( dependsOnMethods = "creatingCustomer" )
	public void fetchAll() {
		ResponseEntity<List> responseEntity = restCustomer.getForEntity(BASE_PATH, List.class);	
		assertThat(responseEntity).isNotNull();
		assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).hasAtLeastOneElementOfType(Object.class);
		
		log.info("Reading all customers in database: "+responseEntity.getBody());
	}

	@Test( dependsOnMethods = "fetchAll", dataProvider = "customerProvider" )
	public void updatingCustomer(Customer customer) {
		// TODO: remove this instruction after the implementation of the method to insert a new Customer
		customer.setId(1);
		URI uri = URI.create(BASE_PATH+"/"+customer.getId());
		customer = fetchCustomer(uri);
		customer.setName("[U] "+customer.getName());
		customer.setEmail("u"+customer.getEmail());

		CustomerDTO dto = new CustomerDTO(customer);
		restCustomer.put(uri, dto);

		Customer updatedCustomer = fetchCustomer(uri);
		assertThat(updatedCustomer).isEqualToComparingOnlyGivenFields(customer,
				"id","name","email","documentNumber","clientType");

		log.info("Updated customer: "+updatedCustomer);
	}

	/*
	 * Invoking this method twice:
	 * 	- first:	to delete customers created in database
	 *  - second:	to try to delete customers that are no longer in database
	 */
	// Method to insert a new customer with no orders not implemented yet
	// For now, this method can't be invoked
	@Test( dependsOnMethods = "updatingCustomer", dataProvider = "customerProvider", invocationCount=2 )
	public void deletingCustomer(Customer customer) {
//		URI uri = URI.create(BASE_PATH+"/"+customer.getId());
//		
//		try { // try to delete the customer from database
//			restCustomer.delete(uri);
//		} catch(HttpClientErrorException ex) { // Customer was not found to be deleted
//			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//			log.info("Deleting customer: Customer is no longer in database: "+customer);
//			return;
//		}
//
//		try { // try to find the deleted customer in database
//			restCustomer.getForEntity(uri, Customer.class);
//		} catch(HttpClientErrorException ex) { // Customer has just been deleted
//			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//			log.info("Deleted customer: "+customer);
//			return;
//		}
//		
//		Fail.fail("Deletion has not worked properly.");
	}

	@Test( dependsOnMethods = "deletingCustomer" )
	public void deletingNotAllowed() {
		int customerId = 1;
		URI uri = URI.create(BASE_PATH+"/"+customerId);
		try { // try to delete the customer from database
			restCustomer.delete(uri);
		} catch(HttpClientErrorException ex) { // Deletion has not been allowed
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		}

		Customer customerRemainsInDatabase = fetchCustomer(customerId);
		log.info("Deleting customer: Not allowed to delete customer: "+customerRemainsInDatabase);
	}


	@Test( dependsOnMethods = "deletingNotAllowed" )
	public void pagingResults() {
// Method to insert a new customer not implemented yet
//		// adding a lot of customers to perform the paging test
//		List<URI> addedCustomerUriList = new ArrayList();
//		for(int i = 1; i < 51; ++i) {
//			URI responseUri = restCustomer.postForLocation(
//					BASE_PATH,
//					Customer.builder().name("test"+i).build());
//			addedCustomerUriList.add(responseUri);
//		}
		
		/*
		 * For now, I am just testing one page.
		 * TODO: Make a test to all pages
		 * TODO: Make tests to all RequestParam
		 */
		//do {
			ResponseEntity<PagedResources<CustomerDTO>> responseEntity =
					restCustomer.exchange(BASE_PATH+"/paging",
	                HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<CustomerDTO>>() {});
			
			assertThat(responseEntity).isNotNull();
			assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
			
			Collection<CustomerDTO> collectionDTO = responseEntity.getBody().getContent();
			log.info("Paging customers in database: "+collectionDTO);
		//} while( ** has more pages ** );

// Not necessary yet, because nothing was added
//		// deleting the customers added to perform this test
//		for (URI uri : addedCustomerUriList) {
//			restCustomer.delete(uri);
//		}
	}
	
	@Test
	public void validatingNameOnInsert() {
		Customer customer = Customer.builder().name("").email("email@email.com").build();
		try {
			creatingCustomer(customer); // must throw an exception
			
			Fail.fail("Customer with an empty name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		customer.setName("a");
		try {
			creatingCustomer(customer); // must throw an exception
			
			Fail.fail("Customer with a short name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		customer.setName(
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
			creatingCustomer(customer); // must throw an exception

			Fail.fail("Customer with a too long name should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}		
	}
	
	@Test
	public void validatingNameOnUpdate() {
		CustomerDTO customerDTO = CustomerDTO.builder().id(1).name("").email("email@email.com").build();
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);

			Fail.fail("Customer should not be updated with an empty name.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);
			
			Fail.fail("Customer should not be updated with a short name.");
		} catch( HttpClientErrorException ex ) {
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
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}		
	}

	@Test
	public void validatingEmailOnInsert() {
		Customer customer = Customer.builder().name("Aaaaaaaaaa Bbbbbbbbb").email("").build();
		try {
			creatingCustomer(customer); // must throw an exception
			
			Fail.fail("Customer with an empty email address should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		customer.setEmail("a");
		try {
			creatingCustomer(customer); // must throw an exception
			
			Fail.fail("Customer with a invalid email address format should not be inserted in database.");
		} catch( HttpClientErrorException ex ) {
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
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
		}
		
		customerDTO.setName("a");
		try {
			// Method updatingCustomer can not be invoked here because it modifies the customer name
			URI uri = URI.create(BASE_PATH+"/"+customerDTO.getId()); // must throw an exception
			restCustomer.put(uri, customerDTO);
			
			Fail.fail("Customer with a invalid email address format should not be updated in database.");
		} catch( HttpClientErrorException ex ) {
			assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			log.info(ex.getMessage());
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

	@DataProvider
	private Customer[] customerProvider() {
		if( !insertedCustomers.isEmpty() ) {
			return insertedCustomers.toArray(new Customer[insertedCustomers.size()]);
		}

		return new Customer[] {
				Customer.builder()
				.name("Aaaaaaaaaa Bbbbbbbbb")
				.email("email@email.com")
				.documentNumber("111.222.333-44")
				.build()
			};
	}
	
}
