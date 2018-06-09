package br.com.technomori.ordermanager.domain;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class OrderItem {
	
	@EmbeddedId
	private OrderItemPK pk;
	
	private Double discount;
	private Integer quantity;
	private Double price;
	
	@Builder
	private OrderItem(Order order, Product product, Double discount, Integer quantity, Double price) {
		pk = OrderItemPK.builder().order(order).product(product).build();
		this.discount = discount;
		this.quantity = quantity;
		this.price = price;
	}
	
	public Double getSubTotal() {
		return (  (price - discount) * quantity  );
	}
	
}

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
@Embeddable
class OrderItemPK implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	private Order order;
	
	@ManyToOne
	private Product product;		
}

