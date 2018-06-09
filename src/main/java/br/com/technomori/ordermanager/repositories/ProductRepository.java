package br.com.technomori.ordermanager.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.technomori.ordermanager.domain.Category;
import br.com.technomori.ordermanager.domain.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {


	/*
	   @Query(
			"SELECT DISTINCT p "+
			"FROM Product p "+
			"INNER JOIN p.categories c "+
			"WHERE p.name LIKE %:productName% AND "+
			"c IN :categories"
		)
	*/
	Page<Product> findDistinctByNameContainingAndCategoriesIn(
			@Param("productName") String productName,
			@Param("categories") List<Category> categories,
			Pageable pageRequest);
}
