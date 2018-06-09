package br.com.technomori.ordermanager.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
	
	@ManyToOne
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

}
