package br.com.technomori.ordermanager.domain;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
@Table(name="ORDER_TABLE")

public class Order {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	@JsonFormat(pattern="yyyy/MM/dd hh:mm a")
	private Date instant;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Customer customer;

	@ManyToOne
	private Address address;

	@OneToOne(mappedBy="order", cascade=CascadeType.ALL)
	@Setter(value=AccessLevel.NONE)
	private Payment payment;
	
	@OneToMany(mappedBy="pk.order", cascade=CascadeType.ALL)
	@Builder.Default
	private List<OrderItem> orderItems = new ArrayList<OrderItem>();

	public Double getTotal() {
		double total = 0d;
		for( OrderItem item : orderItems ) {
			total += item.getSubTotal();
		}
		return total;
	}

	@Override
	public String toString() {
		Locale locale = new Locale("en", "US");
		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		DateFormat dt = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		StringBuilder builder = new StringBuilder();
		builder.append("\nOrder number: #");
		builder.append(getId());
		builder.append(", Instant: ");
		builder.append(dt.format(getInstant()));
		builder.append(", Customer: ");
		builder.append(getCustomer().getName());
		builder.append(", Payment status: ");
		builder.append(getPayment().getPaymentStatus());
		builder.append("\nDetails:\n");
		for( OrderItem item : getOrderItems() ) {
			builder.append(item);
			builder.append("\n");
		}
		builder.append("Total: ");
		builder.append(nf.format(getTotal()));
		return builder.toString();
	}

}
