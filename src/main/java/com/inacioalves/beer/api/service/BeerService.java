package com.inacioalves.beer.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inacioalves.beer.api.dto.BeerDTO;
import com.inacioalves.beer.api.entity.Beer;
import com.inacioalves.beer.api.exception.BeerAlreadyRegisteredException;
import com.inacioalves.beer.api.exception.BeerNotFoundException;
import com.inacioalves.beer.api.exception.BeerStockExceededException;
import com.inacioalves.beer.api.mapper.BeerMapper;
import com.inacioalves.beer.api.repository.BeerRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class BeerService {
	
	private final BeerRepository beerRepository;
	private final BeerMapper beerMapper =BeerMapper.INSTACE;
	
	public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
		verifyIfIsAlreadyRegistered(beerDTO.getName());
		Beer beer = beerMapper.toModel(beerDTO);
		Beer savedBeer= beerRepository.save(beer);
		return beerMapper.toDTO(savedBeer);
	}
	
	public BeerDTO findByName(String name) throws BeerNotFoundException {
		Beer foundBeer = beerRepository.findByName(name)
				.orElseThrow(()-> new BeerNotFoundException(name));
		return beerMapper.toDTO(foundBeer);
		
	}
	
	public List<BeerDTO>listAll(){
		return beerRepository.findAll()
				.stream()
				.map(beerMapper::toDTO)
				.collect(Collectors.toList());
	}
	
	
	 public void deleteById(Long id) throws BeerNotFoundException {
	        verifyIfExists(id);
	        beerRepository.deleteById(id);
	    }
	
	public BeerDTO increment (Long id ,int quantityToIncrement) throws BeerStockExceededException, BeerNotFoundException {
		Beer beerToIncrementStock = verifyIfExists(id);
		int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
		if(quantityAfterIncrement <= beerToIncrementStock.getMax()) {
			beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
			Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
			return beerMapper.toDTO(incrementedBeerStock);
		}
		throw new BeerStockExceededException(id, quantityToIncrement);
	}
	
	
	private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }
	
	 private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
	        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
	        if (optSavedBeer.isPresent()) {
	            throw new BeerAlreadyRegisteredException(name);
	        }
	    }

}
