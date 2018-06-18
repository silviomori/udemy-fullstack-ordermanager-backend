package br.com.technomori.ordermanager.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.technomori.ordermanager.domain.Customer;
import br.com.technomori.ordermanager.domain.Order;

@Repository

public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Transactional(readOnly=true)
	Page<Order> findByCustomer(Customer customer, Pageable pageRequest);
}
