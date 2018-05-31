package br.com.technomori.ordermanager;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.domain.City;
import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.domain.State;
import br.com.technomori.ordermanager.repositories.CategoryRepository;
import br.com.technomori.ordermanager.repositories.CityRepository;
import br.com.technomori.ordermanager.repositories.ProductRepository;
import br.com.technomori.ordermanager.repositories.StateRepository;

@SpringBootApplication
public class OrderManagerApplication implements CommandLineRunner {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private StateRepository stateRepository;
	
	@Autowired
	private CityRepository cityRepository;
	
	
	public static void main(String[] args) {
		SpringApplication.run(OrderManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Category computingCategory = Category.builder().name("Computing").build();
		Category officeCategory = Category.builder().name("Office").build();
		
		categoryRepository.saveAll(Arrays.asList(computingCategory,officeCategory));
		
		
		Product computer = Product.builder()
				.name("Computer")
				.price(2000d)
				.category(computingCategory)
				.build();
		Product printer = Product.builder()
				.name("Printer")
				.price(800d)
				.category(computingCategory)
				.category(officeCategory)
				.build();
		Product mouse = Product.builder()
				.name("Mouse")
				.price(80d)
				.category(computingCategory)
				.build();
		
		productRepository.saveAll(Arrays.asList(computer,printer,mouse));
		
		
		State sp = State.builder().name("SP").build();
		State mg = State.builder().name("MG").build();
		
		stateRepository.saveAll(Arrays.asList(sp,mg));

		City saopaulo = City.builder().name("São Paulo").state(sp).build();
		City campinas = City.builder().name("Campinas").state(sp).build();
		City uberlandia = City.builder().name("Uberlândia").state(mg).build();
		
		cityRepository.saveAll(Arrays.asList(saopaulo,campinas,uberlandia));
		
	}
}
