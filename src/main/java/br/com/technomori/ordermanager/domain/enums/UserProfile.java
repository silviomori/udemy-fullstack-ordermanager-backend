package br.com.technomori.ordermanager.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access=AccessLevel.PRIVATE)

public enum UserProfile {

	ADMIN(1, "ROLE_ADMIN"),
	CUSTOMER(2, "ROLE_CUSTOMER");
	
	private int id;
	private String description;
}
