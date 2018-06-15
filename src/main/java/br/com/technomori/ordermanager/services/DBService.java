package br.com.technomori.ordermanager.services;

import java.util.Arrays;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Address;
import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.domain.City;
import br.com.technomori.ordermanager.domain.CreditCardPayment;
import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.Order;
import br.com.technomori.ordermanager.domain.OrderItem;
import br.com.technomori.ordermanager.domain.Payment;
import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.domain.State;
import br.com.technomori.ordermanager.domain.TicketPayment;
import br.com.technomori.ordermanager.domain.enums.CustomerType;
import br.com.technomori.ordermanager.domain.enums.PaymentStatus;
import br.com.technomori.ordermanager.domain.enums.UserProfile;
import br.com.technomori.ordermanager.repositories.AddressRepository;
import br.com.technomori.ordermanager.repositories.CategoryRepository;
import br.com.technomori.ordermanager.repositories.CityRepository;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.repositories.OrderItemRepository;
import br.com.technomori.ordermanager.repositories.OrderRepository;
import br.com.technomori.ordermanager.repositories.PaymentRepository;
import br.com.technomori.ordermanager.repositories.ProductRepository;
import br.com.technomori.ordermanager.repositories.StateRepository;

@Service

public class DBService {

	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private StateRepository stateRepository;
	@Autowired
	private CityRepository cityRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private PaymentRepository paymentRepository;	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	public boolean instantiateDatabase() {
		Category computingCategory = Category.builder().name("Computing").build();
		Category officeCategory = Category.builder().name("Office").build();
		Category bathAndBeddingCategory = Category.builder().name("Bath & Bedding").build();
		Category techCategory = Category.builder().name("Tech").build();
		Category gardenCategory = Category.builder().name("Garden").build();
		Category decorCategory = Category.builder().name("Decor").build();
		Category healthAndBeautyCategory = Category.builder().name("Health & Beauty").build();
		
		categoryRepository.saveAll(Arrays.asList(
				computingCategory,
				officeCategory,
				bathAndBeddingCategory,
				techCategory,
				gardenCategory,
				decorCategory,
				healthAndBeautyCategory
				));
		
		
		Product computer = Product.builder()
				.name("Computer")
				.price(2000d)
				.category(computingCategory)
				.category(techCategory)
				.build();
		Product printer = Product.builder()
				.name("Printer")
				.price(800d)
				.category(computingCategory)
				.category(officeCategory)
				.category(techCategory)
				.build();
		Product mouse = Product.builder()
				.name("Mouse")
				.price(80d)
				.category(computingCategory)
				.category(techCategory)
				.build();
		Product officeDesk = Product.builder()
				.name("Office Desk")
				.price(300d)	
				.category(officeCategory)
				.build();
		Product bathTowel = Product.builder()
				.name("Bath Towel")
				.price(50d)
				.category(bathAndBeddingCategory)
				.build();
		Product bedSpread = Product.builder()
				.name("Bed Spread")
				.price(200d)
				.category(bathAndBeddingCategory)
				.build();
		Product uhdTv = Product.builder()
				.name("UHD TV 55 in.")
				.price(3900d)
				.category(techCategory)
				.build();
		Product brushCutter = Product.builder()
				.name("Brush Cutter")
				.price(800d)
				.category(gardenCategory)
				.build();
		Product lampshade = Product.builder()
				.name("Lampshade")
				.price(100d)
				.category(decorCategory)
				.build();
		Product pendant = Product.builder()
				.name("Pendant")
				.price(180d)
				.category(decorCategory)
				.build();
		Product shampoo = Product.builder()
				.name("Shampoo")
				.price(10d)
				.category(healthAndBeautyCategory)
				.build();
		
		productRepository.saveAll(Arrays.asList(
				computer,
				printer,
				mouse,
				officeDesk,
				bathTowel,
				bedSpread,
				uhdTv,
				brushCutter,
				lampshade,
				pendant,
				shampoo));
		
		
		State sp = State.builder().name("SP").build();
		State mg = State.builder().name("MG").build();
		
		stateRepository.saveAll(Arrays.asList(sp,mg));

		City saopaulo = City.builder().name("São Paulo").state(sp).build();
		City campinas = City.builder().name("Campinas").state(sp).build();
		City uberlandia = City.builder().name("Uberlândia").state(mg).build();
		
		cityRepository.saveAll(Arrays.asList(saopaulo,campinas,uberlandia));
		
		Customer customer = Customer.builder()
				.name("Sílvio Mori Neto")
				.email("silviomori@gmail.com")
				.password(passwordEncoder.encode("123"))
				.phone("27363323")
				.phone("93838393")
				.documentNumber("36378912377")
				.customerType(CustomerType.INDIVIDUAL)
				.userProfile(UserProfile.CUSTOMER)
				.build();
		
		customerRepository.save(customer);
		
		Address address1 = Address.builder()
				.street("Flower Street")
				.number("300")
				.complement("ap 203")
				.district("Garden City")
				.zipCode("38220834")
				.city(uberlandia)
				.customer(customer)
				.build();
		Address address2 = Address.builder()
				.street("Mattos Avenue")
				.number("105")
				.complement("room 600")
				.district("Downtown")
				.zipCode("38777012")
				.city(saopaulo)
				.customer(customer)
				.build();
		
		addressRepository.saveAll(Arrays.asList(address1,address2));
		
		Customer customer2 = Customer.builder()
				.name("TechnoMori Desenvolvimento de Sistemas")
				.email("technomorisistemas@gmail.com")
				.password(passwordEncoder.encode("123"))
				.phone("28363323")
				.phone("94838393")
				.documentNumber("37378912377")
				.customerType(CustomerType.INDIVIDUAL)
				.userProfile(UserProfile.ADMIN)
				.userProfile(UserProfile.CUSTOMER)
				.build();
		
		customerRepository.save(customer2);
		
		Address address3 = Address.builder()
				.street("5th Avenue")
				.number("64")
				.district("Central Perk")
				.zipCode("38200830")
				.city(saopaulo)
				.customer(customer2)
				.build();
		
		addressRepository.save( address3 );
		
		Order order1 = Order.builder()
				.instant(new Date(System.currentTimeMillis()-(12*24*60*60*1000)))
				.customer(customer)
				.address(address1)
				.build();
		Order order2 = Order.builder()
				.instant(new Date(System.currentTimeMillis()))
				.customer(customer)
				.address(address2)
				.build();
		
		OrderItem orderItem1 = OrderItem.builder()
				.order(order1)
				.product(computer)
				.discount(0d)
				.quantity(1)
				.price(2000d)
				.build();
		OrderItem orderItem2 = OrderItem.builder()
				.order(order1)
				.product(mouse)
				.discount(0d)
				.quantity(2)
				.price(80d)
				.build();
		OrderItem orderItem3 = OrderItem.builder()
				.order(order2)
				.product(printer)
				.discount(12.5)
				.quantity(1)
				.price(800d)
				.build();
		
		Payment pay1 = CreditCardPayment.builder()
				.installments(6)
				.build();
				pay1.setPaymentStatus(PaymentStatus.CLEARED);
				pay1.setOrder(order1);
		Payment pay2 = TicketPayment.builder()
				.expirationDate(new Date(System.currentTimeMillis()+(5*24*60*60*1000)))
				.paymentDate(new Date(System.currentTimeMillis()))
				.build();
				pay2.setPaymentStatus(PaymentStatus.PENDING);
				pay2.setOrder(order2);
				
		/*
		 * References to Payments and OrderItems were not included into
		 * Order entity, then explicit save() methods need to be invoked for them.
		 */
		paymentRepository.saveAll(Arrays.asList(pay1,pay2));
		orderRepository.saveAll(Arrays.asList(order1,order2));
		orderItemRepository.saveAll(Arrays.asList(orderItem1,orderItem2,orderItem3));

		return true;
	}

}
