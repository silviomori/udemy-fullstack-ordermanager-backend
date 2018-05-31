package br.com.technomori.ordermanager.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.repositories.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;
	
	public Category fetch(Integer id) {
		Optional<Category> ret = repository.findById(id);
		return ret.orElse(null);
	}
}
