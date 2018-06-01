package br.com.technomori.ordermanager.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum ClientType {

	PESSOAFISICA(1, "Pessoa Física"),
	PESSOAJURIDICA(2, "Pessoa Jurídica");
	
	private Integer id;
	private String description;

	public static ClientType getById(Integer id) {
		if( id == null ) {
			return null;
		}
	
		for (ClientType clientType : ClientType.values()) {
			if( clientType.getId().equals(id) ) {
				return clientType;
			}
		}
		
		throw new IllegalArgumentException("ClientType not found for id: "+id);
	}
}
