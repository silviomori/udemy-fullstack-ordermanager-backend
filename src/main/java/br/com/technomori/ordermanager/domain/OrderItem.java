package br.com.technomori.ordermanager.domain;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Locale;

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

@Entity
@Getter
@Setter
@EqualsAndHashCode
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
	
	public void setProduct(Product product) {
		pk.setProduct(product);
	}

	@Override
	public String toString() {
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		StringBuilder builder = new StringBuilder();
		builder.append(getProduct().getName());
		builder.append(", Quantity: ");
		builder.append(getQuantity());
		builder.append(", Unit price: ");
		builder.append(nf.format(price));
		builder.append(", Unit discount: ");
		builder.append(nf.format(getDiscount()*getPrice()));
		builder.append(", Subtotal: ");
		builder.append(nf.format(getSubTotal()));
		return builder.toString();
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

