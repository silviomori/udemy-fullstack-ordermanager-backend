package br.com.technomori.ordermanager.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum CustomerType {

	INDIVIDUAL(1, "Individual"),
	CORPORATE(2, "Corporate");
	
	private Integer id;
	private String description;

	public static CustomerType getById(Integer id) {
		if( id == null ) {
			return null;
		}
	
		for (CustomerType customerType : CustomerType.values()) {
			if( customerType.getId().equals(id) ) {
				return customerType;
			}
		}
		
		throw new IllegalArgumentException("CustomerType not found for id: "+id);
	}
}
