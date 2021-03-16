package com.inacioalves.beer.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inacioalves.beer.api.entity.Beer;

public interface BeerRepository extends JpaRepository<Beer, Long>{
	

	Optional<Beer> findByName(String name);
}
