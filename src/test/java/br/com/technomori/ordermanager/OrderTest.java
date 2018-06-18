package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Collection;
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

import br.com.technomori.ordermanager.domain.CreditCardPayment;
import br.com.technomori.ordermanager.domain.Order;
import br.com.technomori.ordermanager.domain.TicketPayment;
import br.com.technomori.ordermanager.dto.InsertOrderDTO;
import br.com.technomori.ordermanager.dto.InsertOrderItemDTO;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@Test(groups = "OrderTest")

public class OrderTest {

	private static Logger log = Logger.getLogger(OrderTest.class.getName());

	private final String BASE_PATH = TestSuite.SERVER_ADDRESS+"/orders";

	private RestTemplate restOrder = RestTemplateFactory.getRestTemplateCustomerProfile();

	@BeforeClass
	public void beforeClass() {
	}

	@Test(dataProvider="creatingOrderProvider")
	public void creatingOrder(InsertOrderDTO dto) {

		URI responseUri = restOrder.postForLocation(BASE_PATH, dto);

		assertThat(responseUri).isNotNull();
		
		ResponseEntity<Order> entityResult = restOrder.getForEntity(responseUri, Order.class);

		log.info("Order created: "+entityResult.getBody());
	}
	
	@Test
	public void testingAccessControlToEndpoints() {
		// POST
		URI uriInsertedOrder_1 = null;
		InsertOrderDTO insertOrderDTO_1 = InsertOrderDTO.builder()
				.customerId(1)
				.customerAddressId(1)
				.paymentType(TicketPayment.IDENTIFIER)
				.orderItem(InsertOrderItemDTO.builder().productId(1).quantity(1).build())
				.build();

		try {
			RestTemplateFactory.getRestTemplateNoProfile().postForLocation(BASE_PATH, insertOrderDTO_1);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}

		RestTemplateFactory.getRestTemplateCustomerProfile().postForLocation(BASE_PATH, insertOrderDTO_1);
		uriInsertedOrder_1 = RestTemplateFactory.getRestTemplateAdminProfile().postForLocation(BASE_PATH, insertOrderDTO_1);

		URI uriInsertedOrder_2 = null;
		InsertOrderDTO insertOrderDTO_2 = InsertOrderDTO.builder()
				.customerId(2)
				.customerAddressId(1)
				.paymentType(TicketPayment.IDENTIFIER)
				.orderItem(InsertOrderItemDTO.builder().productId(1).quantity(1).build())
				.build();

		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().postForLocation(BASE_PATH, insertOrderDTO_2);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}
		
		uriInsertedOrder_2 = RestTemplateFactory.getRestTemplateAdminProfile().postForLocation(BASE_PATH, insertOrderDTO_2);

	
		// GET - by ID
		try {
			RestTemplateFactory.getRestTemplateNoProfile().getForEntity(uriInsertedOrder_1, Object.class);
			
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}

		RestTemplateFactory.getRestTemplateCustomerProfile().getForEntity(uriInsertedOrder_1, Object.class);
		
		try {
			RestTemplateFactory.getRestTemplateCustomerProfile().getForEntity(uriInsertedOrder_2, Object.class);
				
			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}

		RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(uriInsertedOrder_1, Object.class);
		RestTemplateFactory.getRestTemplateAdminProfile().getForEntity(uriInsertedOrder_2, Object.class);

	}
	
	@Test
	public void fetchOrdersByCustomer() {
		
		try {
			RestTemplateFactory.getRestTemplateNoProfile().getForEntity(BASE_PATH+"/paging", Object.class);

			Fail.fail("Access should be forbidden");
		} catch( HttpClientErrorException e) {
			assertThat(e.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		}


		/*
		 * For now, I am just testing one page.
		 * TODO: Make a test to all pages
		 * TODO: Make tests to all RequestParam
		 */
		//do {
			ResponseEntity<PagedResources<Order>> responseEntity =
					RestTemplateFactory.getRestTemplateCustomerProfile().exchange(BASE_PATH+"/paging?pageNumber=0&linerPerPage=5",
	                HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<Order>>() {});
			
			assertThat(responseEntity).isNotNull();
			assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
			
			Collection<Order> ordersList = responseEntity.getBody().getContent();
			log.info("Paging orders in database: "+ordersList);
		//} while( ** has more pages ** );

	}

	@DataProvider
	private InsertOrderDTO[] creatingOrderProvider() {
		return new InsertOrderDTO[] {
				InsertOrderDTO.builder()
					.customerId(1)
					.customerAddressId(1)
					.paymentType(TicketPayment.IDENTIFIER)
					.orderItem(InsertOrderItemDTO.builder().productId(1).quantity(1).build())
					.orderItem(InsertOrderItemDTO.builder().productId(2).quantity(3).build())
					.build(),
				InsertOrderDTO.builder()
					.customerId(1)
					.customerAddressId(2)
					.paymentType(CreditCardPayment.IDENTIFIER)
					.orderItem(InsertOrderItemDTO.builder().productId(3).quantity(5).build())
					.orderItem(InsertOrderItemDTO.builder().productId(4).quantity(5).build())
					.build()

		};
	}
}
