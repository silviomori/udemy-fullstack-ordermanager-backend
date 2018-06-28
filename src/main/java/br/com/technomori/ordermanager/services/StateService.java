package br.com.technomori.ordermanager.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.technomori.ordermanager.domain.State;
import br.com.technomori.ordermanager.repositories.StateRepository;

@Service

public class StateService {

	@Autowired
	private StateRepository repository;
	
	public List<State> fetchAll() {
		List<State> list = repository.findAll();
		/*List<StateDTO> dtoList = stateList.stream()
				.map(state -> new StateDTO(state))
				.collect(Collectors.toList());
		return dtoList;*/
		return list;
	}

}
