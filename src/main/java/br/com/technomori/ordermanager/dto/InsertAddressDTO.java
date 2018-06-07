package br.com.technomori.ordermanager.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class InsertAddressDTO {

	private String street;
	private String number;
	private String complement;
	private String district;
	private String zipCode;
	private Integer cityId;

}
