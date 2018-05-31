package br.com.technomori.ordermanager.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.services.CategoryService;

@RestController
@RequestMapping(value="/categories")
public class CategoryResource {

	@Autowired
	private CategoryService service;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<?> fetch(@PathVariable Integer id) {
		Category category = service.fetch(id);
		return ResponseEntity.ok().body(category);
	}

	
}
