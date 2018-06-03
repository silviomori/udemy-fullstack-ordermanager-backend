package br.com.technomori.ordermanager.dto;

import br.com.technomori.ordermanager.domain.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@Builder
public class CategoryDTO {
	private Integer id;
	private String name;
	
	public CategoryDTO(Category category) {
		id = category.getId();
		name = category.getName();
	}

}
