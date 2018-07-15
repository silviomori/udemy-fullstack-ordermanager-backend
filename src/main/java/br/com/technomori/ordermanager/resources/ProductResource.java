package br.com.technomori.ordermanager.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.technomori.ordermanager.domain.Product;
import br.com.technomori.ordermanager.dto.ProductDTO;
import br.com.technomori.ordermanager.resources.util.URL;
import br.com.technomori.ordermanager.services.ProductService;

@RestController
@RequestMapping(value="/products")
public class ProductResource {

	@Autowired
	private ProductService service;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<?> fetch(@PathVariable Integer id) {
		Product order = service.fetch(id);
		return ResponseEntity.ok().body(order);
	}

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<Page<ProductDTO>> pagingAll(
			@RequestParam(value="productName", defaultValue="") String productName,
			@RequestParam(value="categoryId", defaultValue="") String categoryId,
			@RequestParam(value="pageNumber", defaultValue="0") Integer pageNumber,
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
			@RequestParam(value="direction", defaultValue="ASC") String direction,
			@RequestParam(value="orderBy", defaultValue="name") String ... orderBy) {
		
		String productNameDecoded = URL.decodeParam(productName);
		List<Integer> categoryIdList = URL.decodeIntList(categoryId);
		
		Page<Product> pageProduct = service.search(
				productNameDecoded,
				categoryIdList,
				pageNumber, linesPerPage, direction, orderBy);
		
		Page<ProductDTO> pageDto = pageProduct.map( product -> new ProductDTO(product) );
		return ResponseEntity.ok().body(pageDto);
	}

}
