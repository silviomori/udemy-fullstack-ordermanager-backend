package br.com.technomori.ordermanager.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.Length;

import br.com.technomori.ordermanager.domain.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class CategoryDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	@NotEmpty(message="Required field.")
	@Length(min=5,max=80, message="Length must be between 5 and 80.")
	private String name;
	
	public CategoryDTO(Category category) {
		id = category.getId();
		name = category.getName();
	}

}
