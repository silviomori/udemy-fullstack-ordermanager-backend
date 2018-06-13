package br.com.technomori.ordermanager.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.technomori.ordermanager.domain.Address;
import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.Order;
import br.com.technomori.ordermanager.domain.OrderItem;
import br.com.technomori.ordermanager.domain.Payment;
import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.domain.TicketPayment;
import br.com.technomori.ordermanager.domain.enums.PaymentStatus;
import br.com.technomori.ordermanager.dto.InsertOrderDTO;
import br.com.technomori.ordermanager.repositories.OrderRepository;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;

	@Autowired
	private ProductService productService;

	@Autowired
	private TicketService ticketService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private ApplicationContext ctx;

	public Order fetch(Integer id) throws ObjectNotFoundException {

		Optional<Order> ret = repository.findById(id);

		return ret.orElseThrow(
				() -> new ObjectNotFoundException("Object not found: TYPE: " + Order.class.getName() + ", ID: " + id));

	}

	@Transactional
	public Order insert(Order order) {
		// Forcing to insert a new Order instead of updating
		order.setId(null);
		order.setInstant(new Date());

		order.getPayment().setPaymentStatus(PaymentStatus.PENDING);
		boolean isTicketPayment = (order.getPayment() instanceof TicketPayment);
		if (isTicketPayment) {
			ticketService.fill((TicketPayment) order.getPayment(), order.getInstant());
		}

		List<OrderItem> orderItems = order.getOrderItems();
		for (OrderItem orderItem : orderItems) {
			Product product = orderItem.getProduct();
			product = productService.fetch(product.getId());
			
			orderItem.setProduct(product);
			orderItem.setPrice(product.getPrice());

			if (isTicketPayment) {
				orderItem.addDiscount(0.05);
			}
		}

		/*
		 * Order has references to OrderItem list and to Payment entities, thus this
		 * operation will be cascade to them. Therefore, it is not necessary call
		 * specific save() methods for the referenced entities.
		 */
		order = repository.save(order);

		order.setCustomer(customerService.fetch(order.getCustomer().getId()));

		emailService.sendEmail(order);

		return order;
	}

	public Order getOrderFromInsertDTO(InsertOrderDTO dto) {

		/*
		 * Cross-reference between Order and Payment and between Order and OrderItem is
		 * necessary to cascade the insert operation. Payment and OrderItem entities has
		 * Foreign Key to Order, thus they must know Order entity. Order entity can
		 * cascade all operations to Payment and OrderItem, thus if these entities are
		 * referenced by Order, then it is not necessary call save() method to them. If
		 * Order does not have references to Payment and OrderItem, then it is necessary
		 * call explicitly save() methods for these entities.
		 */

		Order order = Order.builder().customer(Customer.builder().id(dto.getCustomerId()).build())
				.address(Address.builder().id(dto.getCustomerAddressId()).build())
				.payment((Payment) ctx.getBean(dto.getPaymentType())).build();
		order.getPayment().setOrder(order);

		List<OrderItem> orderItems = dto.getOrderItems().stream()
				.map(item -> OrderItem.builder().product(Product.builder().id(item.getProductId()).build()).order(order)
						.quantity(item.getQuantity()).build())
				.collect(Collectors.toList());
		order.setOrderItems(orderItems);

		return order;
	}
}
