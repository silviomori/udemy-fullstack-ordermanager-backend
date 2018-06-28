package br.com.technomori.ordermanager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.technomori.ordermanager.domain.City;

@Repository

public interface CityRepository extends JpaRepository<City, Integer> {

	@Transactional(readOnly=true)
	@Query("SELECT c FROM City c WHERE c.state.id = :stateId ORDER BY c.name")
	List<City> findByState(@Param("stateId") Integer stateId);

}
