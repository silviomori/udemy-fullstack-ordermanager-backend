package br.com.technomori.ordermanager.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.repositories.CategoryRepository;
import br.com.technomori.ordermanager.repositories.ProductRepository;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private CategoryRepository categoryRepo;
	
	public Product fetch(Integer id) throws ObjectNotFoundException {
		
		Optional<Product> ret = productRepo.findById(id);

		return ret.orElseThrow(
			() -> new ObjectNotFoundException("Object not found: TYPE: "+Product.class.getName()+", ID: "+id)
		);

	}
	
	
	public Page<Product> search(String productName, List<Integer> categoryIds, 
			Integer pageNumber, Integer linesPerPage, String direction, String ... orderBy) {
		PageRequest pageRequest = PageRequest.of(pageNumber, linesPerPage, Direction.valueOf(direction), orderBy);
		
		List<Category> categories = categoryRepo.findAllById(categoryIds);
		
		return productRepo.findDistinctByNameContainingAndCategoriesIn(productName, categories, pageRequest);
	}

}
