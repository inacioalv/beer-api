package com.inacioalves.beer.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.inacioalves.beer.api.dto.BeerDTO;
import com.inacioalves.beer.api.entity.Beer;

@Mapper
public interface BeerMapper {
	
	BeerMapper INSTACE =Mappers.getMapper(BeerMapper.class);
	
	Beer toModel(BeerDTO beerDTO);
	
	BeerDTO toDTO(Beer beer);
}
