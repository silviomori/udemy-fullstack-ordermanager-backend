package br.com.technomori.ordermanager;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.repositories.CategoryRepository;
import br.com.technomori.ordermanager.repositories.ProductRepository;

@SpringBootApplication
public class OrderManagerApplication implements CommandLineRunner {

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	
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
		
	}
}
