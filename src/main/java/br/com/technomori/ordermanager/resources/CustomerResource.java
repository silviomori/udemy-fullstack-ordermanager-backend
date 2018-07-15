package br.com.technomori.ordermanager.resources;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.dto.InsertCustomerDTO;
import br.com.technomori.ordermanager.services.CustomerService;

@RestController
@RequestMapping(value="/customers")
public class CustomerResource {

	@Autowired
	private CustomerService service;
	
	@RequestMapping(method=RequestMethod.GET, value="/{id}")
	public ResponseEntity<?> fetch(@PathVariable Integer id) {
		Customer customer = service.fetch(id);
		return ResponseEntity.ok().body(customer);
	}

	@RequestMapping( method = RequestMethod.GET, value="/email")
	public ResponseEntity<Customer> fetchByEmail(@RequestParam(value="value") String email) {
		Customer customer = service.fetchByEmail(email);
		return ResponseEntity.ok(customer);
	}
	
	@RequestMapping(method=RequestMethod.GET)
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<List<CustomerDTO>> fetchAll() {
		List<CustomerDTO> list = service.fetchAll();
		return ResponseEntity.ok().body(list);
	}

	@RequestMapping(method=RequestMethod.GET, value="/paging")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Page<CustomerDTO>> pagingAll(
			@RequestParam(value="pageNumber", defaultValue="0") Integer pageNumber,
			@RequestParam(value="linesPerPage", defaultValue="24") Integer linesPerPage,
			@RequestParam(value="direction", defaultValue="ASC") String direction,
			@RequestParam(value="orderBy", defaultValue="name") String ... orderBy) {
		Page<CustomerDTO> page = service.pagingAll(pageNumber, linesPerPage, direction, orderBy);
		return ResponseEntity.ok().body(page);
	}


	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody InsertCustomerDTO insertDTO) {
		// Constraints in a @Valid object must be accessed in this method, otherwise they will not be validated
		Customer customer = service.getCustomerFromInsertDTO(insertDTO);
		customer = service.insert(customer);
		URI uriResponse = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(customer.getId())
				.toUri();
		return ResponseEntity.created(uriResponse).build();
	}
	

	@RequestMapping(value="/picture", method=RequestMethod.POST)
	public ResponseEntity<Void> uploadProfilePicture(@Valid @RequestBody MultipartFile file) {
		// Constraints in a @Valid object must be accessed in this method, otherwise they will not be validated
		URI uri = service.uploadProfilePicture(file);
		return ResponseEntity.created(uri).build();
	}
	

	@RequestMapping(method=RequestMethod.PUT, value="/{id}")
	public ResponseEntity<Void> update(@PathVariable Integer id, @Valid @RequestBody CustomerDTO customerDTO) {
		// Constraints in a @Valid object must be accessed in this method, otherwise they will not be validated
		Customer customer = service.getCustomerFromDTO(customerDTO);
		
		customer.setId(id);
		service.update(customer);
		return ResponseEntity.noContent().build();
	}

	@RequestMapping(method=RequestMethod.DELETE, value="/{id}")
	@PreAuthorize("hasAnyRole('ADMIN')")
	public ResponseEntity<Customer> delete(@PathVariable Integer id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}


}
