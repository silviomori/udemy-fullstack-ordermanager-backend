package br.com.technomori.ordermanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.technomori.ordermanager.domain.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

}
