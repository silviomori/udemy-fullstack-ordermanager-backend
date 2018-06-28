package br.com.technomori.ordermanager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.City;
import br.com.technomori.ordermanager.repositories.CityRepository;

@Service

public class CityService {

	@Autowired
	private CityRepository repository;
	
	public List<City> fetchByState(Integer stateId) {
		List<City> list = repository.findByState(stateId);
		/*List<CityDTO> dtoList = stateList.stream()
				.map(city -> new CityDTO(city))
				.collect(Collectors.toList());
		return dtoList;*/
		return list;
	}

}
