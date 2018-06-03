package br.com.technomori.ordermanager.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.dto.CategoryDTO;
import br.com.technomori.ordermanager.repositories.CategoryRepository;
import br.com.technomori.ordermanager.services.exceptions.DataIntegrityException;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	public Category fetch(Integer id) throws ObjectNotFoundException {
		
		Optional<Category> ret = repository.findById(id);

		return ret.orElseThrow(
			() -> new ObjectNotFoundException("Object not found: TYPE: "+Category.class.getName()+", ID: "+id)
		);

	}
	
	public List<CategoryDTO> fetchAll() {
		List<Category> categoryList = repository.findAll();
		List<CategoryDTO> categoryDTOList = categoryList.stream()
				.map(category -> new CategoryDTO(category))
				.collect(Collectors.toList());
		return categoryDTOList;
	}

	public Category insert(Category category) {
		//Forcing to insert a new Category instead of updating
		category.setId(null);
		return repository.save(category);
	}
	
	public void update(Category category) {
		fetch(category.getId()); // Throws an exception if category is not found
		repository.save(category);
	}

	public void delete(Integer id) {
		Category categoryToBeDeleted = fetch(id); // Throws an exception if category is not found
		try {
			repository.delete(categoryToBeDeleted);
		} catch( DataIntegrityViolationException ex ) {
			throw new DataIntegrityException("It is not allowed to delete a Category containing Products.");
		}
	}

}
