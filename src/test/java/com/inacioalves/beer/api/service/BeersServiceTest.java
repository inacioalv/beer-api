package com.inacioalves.beer.api.service;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.inacioalves.beer.api.builder.BeerDTOBuilder;
import com.inacioalves.beer.api.dto.BeerDTO;
import com.inacioalves.beer.api.entity.Beer;
import com.inacioalves.beer.api.exception.BeerAlreadyRegisteredException;
import com.inacioalves.beer.api.exception.BeerNotFoundException;
import com.inacioalves.beer.api.exception.BeerStockExceededException;
import com.inacioalves.beer.api.mapper.BeerMapper;
import com.inacioalves.beer.api.repository.BeerRepository;

@ExtendWith(MockitoExtension.class)
public class BeersServiceTest {
	
	private static final long INVALID_BEER_ID = 1L;

	
	@Mock
	private BeerRepository beerRepository;
	
	private BeerMapper beerMapper = BeerMapper.INSTACE;
	
	@InjectMocks
	private BeerService beerService;
	
	
	
	@Test
	void whenBeerInformedThenItshouldBeCreated() throws BeerAlreadyRegisteredException {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedSavedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
		when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);
		
		//then
		BeerDTO createdBeerDTO = beerService.createBeer(expectedBeerDTO);
		
		
		assertThat(createdBeerDTO.getId(), is(equalTo(createdBeerDTO.getId())));
		assertThat(createdBeerDTO.getName(), is(equalTo(createdBeerDTO.getName())));
		assertThat(createdBeerDTO.getQuantity(), is(equalTo(createdBeerDTO.getQuantity())));
		
		
	}
	
	
	
	@Test
	void whenAlreadyRegisteredBeerInformdThenAnExeptionShouldBeThrown() {
		
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer duplicatedBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));
		
	
		//then
		assertThrows(BeerAlreadyRegisteredException.class,()-> beerService.createBeer(expectedBeerDTO));
	}
	
	
	@Test
	void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		//when
		when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));
		
		//then
		BeerDTO foundBeerDTO = beerService.findByName(expectedFoundBeerDTO.getName());
		
		assertThat(foundBeerDTO, is(equalTo(expectedFoundBeerDTO)));
	
	}
	@Test
	void whenNotRegisteredBeerNameIsGivenThenThrowAnExeption() {	
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		

		//when
		when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());
		
	
		//then
		assertThrows(BeerNotFoundException.class, ()  -> beerService.findByName(expectedFoundBeerDTO.getName()));
	
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAListOfBeers() {
		//given
		BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
		
		
		//when
		when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));
		
	
		//then
		List<BeerDTO> foundListBeersDTO = beerService.listAll();
		
		assertThat(foundListBeersDTO, is(not(empty())));
		assertThat(foundListBeersDTO.get(0),is(equalTo(expectedFoundBeerDTO)));
		
		
	}
	
	@Test
	void whenListBeerIsCalledThenReturnAnEmptyListOfBeers() {
		//when
		when(beerRepository.findAll()).thenReturn(Collections.emptyList());
		
		//then
		List<BeerDTO> foundListBeersDTO= beerService.listAll();
		
		assertThat(foundListBeersDTO, is(empty()));
		
		
	}
	
	@Test
	void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException {
		//given
		BeerDTO expectedDeletedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expectedDeletedBeer = beerMapper.toModel(expectedDeletedBeerDTO);
				
		
		//when
		when(beerRepository.findById(expectedDeletedBeerDTO.getId())).thenReturn(Optional.of(expectedDeletedBeer));
		doNothing().when(beerRepository).deleteById(expectedDeletedBeerDTO.getId());
	
	
	   //then
		beerService.deleteById(expectedDeletedBeerDTO.getId());
		
		verify(beerRepository,times(1)).findById(expectedDeletedBeerDTO.getId());
		verify(beerRepository,times(1)).deleteById(expectedDeletedBeerDTO.getId());
	
	
	}
	
	@Test
	void whenIncrementIsCalledThenIncrementBeerStock() throws BeerStockExceededException, BeerNotFoundException {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expecteddBeer = beerMapper.toModel(expectedBeerDTO);
		
		
		//when
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expecteddBeer));
		when(beerRepository.save(expecteddBeer)).thenReturn(expecteddBeer);
		
		int quantityToIncrement=10;
		int expctendQuantityAfterIncrement= expectedBeerDTO.getQuantity() + quantityToIncrement;
		
		//then
		BeerDTO icrementdBeerDTO = beerService.increment(expectedBeerDTO.getId(), quantityToIncrement);
		
		assertThat(expctendQuantityAfterIncrement, equalTo(icrementdBeerDTO.getQuantity()));
		assertThat(expctendQuantityAfterIncrement, lessThan(expectedBeerDTO.getMax()));
	}
	
	
	@Test
	void whenIncrementIsGreatherThanMaxThenThrowException() {
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expecteddBeer = beerMapper.toModel(expectedBeerDTO);
		
		//when
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expecteddBeer));
		
		int quantityToIncrement=80;
		
		assertThrows(BeerStockExceededException.class, ()-> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
		
	
	
	}
	
	@Test
	void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
		
		//given
		BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		Beer expecteddBeer = beerMapper.toModel(expectedBeerDTO);
				
		//when
		when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expecteddBeer));
		
		int quantityToIncrement=45;
		
		assertThrows(BeerStockExceededException.class, ()-> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
		
	}
	
	
	
	

}
