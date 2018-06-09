package br.com.technomori.ordermanager.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder

public class InsertOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer customerId;
	private Integer customerAddressId;
	
	private String paymentType;
	private Integer installments;
	
	@Singular
	private List<InsertOrderItemDTO> orderItems;

}
