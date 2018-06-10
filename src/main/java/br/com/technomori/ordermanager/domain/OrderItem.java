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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor

public class OrderItem {
	
	@EmbeddedId
	private OrderItemPK pk;
	
	//Percentage value, e.g.: 5% --> 0.05d
	private double discount;
	private int quantity = 0;
	private double price = 0d;
	
	@Builder
	private OrderItem(Order order, Product product, double discount, int quantity, double price) {
		pk = OrderItemPK.builder().order(order).product(product).build();
		this.discount = discount;
		this.quantity = quantity;
		this.price = price;
	}
	
	public Double getSubTotal() {
		return (  price * (1-discount) * quantity  );
	}

	// Add discount over the value already set
	public void addDiscount(double discount) {
		this.discount += discount;
	}

	public Product getProduct() {
		return pk.getProduct();
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

