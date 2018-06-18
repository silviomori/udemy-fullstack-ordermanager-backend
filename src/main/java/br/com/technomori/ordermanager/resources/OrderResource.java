package br.com.technomori.ordermanager.resources;

import java.net.URI;

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

import br.com.technomori.ordermanager.domain.Order;
import br.com.technomori.ordermanager.dto.InsertOrderDTO;
import br.com.technomori.ordermanager.services.OrderService;

@RestController
@RequestMapping(value="/orders")

public class OrderResource {

	@Autowired
	private OrderService service;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<?> fetch(@PathVariable Integer id) {
		Order order = service.fetch(id);
		return ResponseEntity.ok().body(order);
	}

	@RequestMapping(method=RequestMethod.GET, value="/paging")
	public ResponseEntity<Page<Order>> pagingAll(
			@RequestParam(value="pageNumber", defaultValue="0") Integer pageNumber,
			@RequestParam(value="linerPerPage", defaultValue="24") Integer linesPerPage,
			@RequestParam(value="direction", defaultValue="DESC") String direction,
			@RequestParam(value="orderBy", defaultValue="instant") String ... orderBy) {
		Page<Order> page = service.pagingAllByUser(pageNumber, linesPerPage, direction, orderBy);
		return ResponseEntity.ok().body(page);
	}

	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@RequestBody InsertOrderDTO dto) {
		Order order = service.getOrderFromInsertDTO(dto);
		order = service.insert(order);
		URI uriResponse = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(order.getId())
				.toUri();
		return ResponseEntity.created(uriResponse).build();
	}

}
