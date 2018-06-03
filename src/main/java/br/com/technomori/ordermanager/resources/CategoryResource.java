package br.com.technomori.ordermanager.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.dto.CategoryDTO;
import br.com.technomori.ordermanager.services.CategoryService;

@RestController
@RequestMapping(value="/categories")
public class CategoryResource {

	@Autowired
	private CategoryService service;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<Category> fetch(@PathVariable Integer id) {
		Category category = service.fetch(id);
		return ResponseEntity.ok().body(category);
	}

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<CategoryDTO>> fetchAll() {
		List<CategoryDTO> list = service.fetchAll();
		return ResponseEntity.ok().body(list);
	}

	@RequestMapping(method=RequestMethod.GET, value="/paging")
	public ResponseEntity<Page<CategoryDTO>> pagingAll(
			@RequestParam(value="pageNumber", defaultValue="0") Integer pageNumber,
			@RequestParam(value="linerPerPage", defaultValue="24") Integer linesPerPage,
			@RequestParam(value="direction", defaultValue="ASC") String direction,
			@RequestParam(value="orderBy", defaultValue="name") String ... orderBy) {
		Page<CategoryDTO> page = service.pagingAll(pageNumber, linesPerPage, direction, orderBy);
		return ResponseEntity.ok().body(page);
	}

	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody Category category) {
		category = service.insert(category);
		URI uriResponse = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(category.getId())
				.toUri();
		return ResponseEntity.created(uriResponse).build();
	}
	
	@RequestMapping(method=RequestMethod.PUT, value="/{id}")
	public ResponseEntity<Void> update(@PathVariable Integer id, @RequestBody Category category) {
		category.setId(id);
		service.update(category);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	public ResponseEntity<Category> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
