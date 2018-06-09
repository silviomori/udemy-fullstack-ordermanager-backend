package br.com.technomori.ordermanager;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.logging.Logger;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;
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
@Test(groups = "OrderTest" )

public class OrderTest {

	private static Logger log = Logger.getLogger(OrderTest.class.getName());

	private final String BASE_PATH = "http://localhost:8080/orders";

	private RestTemplate restOrder;

	@BeforeClass
	public void beforeClass() {
		restOrder = new RestTemplate();
	}

	@Test(dataProvider="creatingOrderProvider")
	public void creatingOrder(InsertOrderDTO dto) {

		URI responseUri = restOrder.postForLocation(BASE_PATH, dto);

		assertThat(responseUri).isNotNull();
		
		ResponseEntity<Order> entityResult = restOrder.getForEntity(responseUri, Order.class);

		log.info("Order created: "+entityResult.getBody());
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
