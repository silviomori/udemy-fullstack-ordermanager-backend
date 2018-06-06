package br.com.technomori.ordermanager.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.dto.CustomerDTO;
import br.com.technomori.ordermanager.repositories.CustomerRepository;
import br.com.technomori.ordermanager.services.exceptions.DataIntegrityException;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository repository;
	
	public Customer fetch(Integer id) throws ObjectNotFoundException {
		
		Optional<Customer> ret = repository.findById(id);

		return ret.orElseThrow(
			() -> new ObjectNotFoundException("Object not found: TYPE: "+Customer.class.getName()+", ID: "+id)
		);

	}
	
	public List<CustomerDTO> fetchAll() {
		List<Customer> customerList = repository.findAll();
		List<CustomerDTO> customerDTOList = customerList.stream()
				.map(customer -> new CustomerDTO(customer))
				.collect(Collectors.toList());
		return customerDTOList;
	}

	/*
	 * Direction can be: "ASC" or "DESC"
	 */
	public Page<CustomerDTO> pagingAll(Integer pageNumber, Integer linesPerPage, String direction, String ... orderBy) {
		PageRequest pageRequest = PageRequest.of(pageNumber, linesPerPage, Direction.valueOf(direction), orderBy);
		Page<Customer> customerPage = repository.findAll(pageRequest);
		Page<CustomerDTO> customerDTOPage = customerPage
				.map(customer -> new CustomerDTO(customer));
		return customerDTOPage;
	}

	public Customer insert(Customer customer) {
		throw new RuntimeException("Method not implemented yet: CustomerService.insert(Customer)");
//		//Forcing to insert a new Customer instead of updating
//		customer.setId(null);
//		return repository.save(customer);
	}

	public void update(Customer customer) {
		Customer customerToBeUpdated = fetch(customer.getId()); // Throws an exception if customer is not found
		customerUpdateData(customerToBeUpdated,customer);
		repository.save(customerToBeUpdated);
	}

	private void customerUpdateData(Customer customerToBeUpdated, Customer customerWithNewData) {
		// Only those information is allowed to be updated
		customerToBeUpdated.setName(customerWithNewData.getName());
		customerToBeUpdated.setEmail(customerWithNewData.getEmail());
	}

	public void delete(Integer id) {
		Customer customerToBeDeleted = fetch(id); // Throws an exception if customer is not found
		try {
			repository.delete(customerToBeDeleted);
		} catch( DataIntegrityViolationException ex ) {
			throw new DataIntegrityException("It is not allowed to delete customer who has put orders.");
		}
	}


	public Customer getCustomerFromDTO(CustomerDTO dto) {
		return Customer.builder()
				.id(dto.getId())
				.name(dto.getName())
				.email(dto.getEmail())
				.build();
	}

}
