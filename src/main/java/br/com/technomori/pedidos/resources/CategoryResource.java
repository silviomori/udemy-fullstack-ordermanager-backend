package br.com.technomori.pedidos.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.technomori.pedidos.domain.Category;

@RestController
@RequestMapping(value="/categories")
public class CategoryResource {

	@RequestMapping(method=RequestMethod.GET)
	public List<Category> listar() {

		Category cat1 = new Category(1, "Computing");
		Category cat2 = new Category(2, "Office");
		
		List<Category> categoryList = new ArrayList<Category>();
		categoryList.add(cat1);
		categoryList.add(cat2);
		
		return categoryList;
	}

}
