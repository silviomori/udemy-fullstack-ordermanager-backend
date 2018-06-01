package br.com.technomori.ordermanager.domain.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum PaymentStatus {

	PENDING(1,"Pending"),
	CLEARED(2,"Cleared"),
	CANCELED(3,"Canceled");
	
	private Integer id;
	private String description;
}
