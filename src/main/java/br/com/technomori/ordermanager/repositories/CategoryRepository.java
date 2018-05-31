package br.com.technomori.ordermanager.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.technomori.ordermanager.domain.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
