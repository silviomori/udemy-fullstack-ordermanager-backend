package br.com.technomori.ordermanager.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import br.com.technomori.ordermanager.domain.City;
import br.com.technomori.ordermanager.domain.State;
import br.com.technomori.ordermanager.services.CityService;
import br.com.technomori.ordermanager.services.StateService;

@RestController
@RequestMapping(value="/states")

public class StateResource {

	@Autowired
	private StateService stateService;

	@Autowired
	private CityService cityService;
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<State>> fetchAllStates() {
		List<State> list = stateService.fetchAll();
		return ResponseEntity.ok().body(list);
	}

	
	@RequestMapping(method=RequestMethod.GET, value="/{stateId}/cities")
	public ResponseEntity<List<City>> fetchAllCities(@PathVariable Integer stateId) {
		List<City> list = cityService.fetchByState(stateId);
		return ResponseEntity.ok().body(list);
	}

}
