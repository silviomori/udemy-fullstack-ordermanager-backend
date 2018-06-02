package br.com.technomori.ordermanager.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.Order;
import br.com.technomori.ordermanager.repositories.OrderRepository;
import br.com.technomori.ordermanager.services.exceptions.ObjectNotFoundException;

@Service
public class OrderService {

	@Autowired
	private OrderRepository repository;
	
	public Order fetch(Integer id) throws ObjectNotFoundException {
		
		Optional<Order> ret = repository.findById(id);

		return ret.orElseThrow(
			() -> new ObjectNotFoundException("Object not found: TYPE: "+Order.class.getName()+", ID: "+id)
		);

	}
}
