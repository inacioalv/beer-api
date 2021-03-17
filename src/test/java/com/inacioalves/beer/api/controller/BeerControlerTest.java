package com.inacioalves.beer.api.controller;

import static com.inacioalves.beer.api.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.inacioalves.beer.api.builder.BeerDTOBuilder;
import com.inacioalves.beer.api.dto.BeerDTO;
import com.inacioalves.beer.api.dto.QuantityDTO;
import com.inacioalves.beer.api.exception.BeerNotFoundException;
import com.inacioalves.beer.api.exception.BeerStockExceededException;
import com.inacioalves.beer.api.service.BeerService;


@ExtendWith(MockitoExtension.class)
public class BeerControlerTest {
	
	private static final String BEER_API_URL_PATH = "/api/v1/beers";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2l;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
//    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenABeerIsCreated() throws Exception {
        BeerDTO beerDTO = beerDTOBuilder();

        // when
        when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }
    
    
    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
    	BeerDTO beerDTO = beerDTOBuilder();
        beerDTO.setBrand(null);
        
        // then
        mockMvc.perform(post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(beerDTO)))
                .andExpect(status().isCreated());
    	
    }
    
    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
    	BeerDTO beerDTO = beerDTOBuilder();
        
        //when
        when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);
        
     // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
		        .contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$.name", is(beerDTO.getName())))
		        .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
		        .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }
    
    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
    	BeerDTO beerDTO = beerDTOBuilder();
        
        //when
        when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);
        
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
		        .contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isNotFound());
    	
    }
    
    @Test
    void whenGETListWithBeersIsCalledThenOkStatusIsReturned() throws Exception {
    	// given
        BeerDTO beerDTO = beerDTOBuilder();
        
        //then
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));
        
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
        		.contentType(MediaType.APPLICATION_JSON))
		        .andExpect(status().isOk())
		        .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
		        .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
		        .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
        		
        
    	
    }

	
    
    
    @Test
    void whenGETListWithoutBeersIsCalledThenOkStatusIsReturned() throws Exception {
    	// given
        BeerDTO beerDTO = beerDTOBuilder();
        
        //then
        when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));
        
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
        		.contentType(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk());
        
    }
    
    
    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
    	// given
        BeerDTO beerDTO = beerDTOBuilder();
        
        
        //when
        doNothing().when(beerService).deleteById(beerDTO.getId());
        
        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + beerDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
    	//when
    	doThrow(BeerNotFoundException.class).when(beerService).deleteById(INVALID_BEER_ID);
   
    
    	//THEN
    	 mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                 .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(status().isNotFound());
    
    }
    
    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
    	QuantityDTO quantityDTO =QuantityDTO.builder()
    			.quantity(10)
    			.build();
    	
    	// given
        BeerDTO beerDTO = beerDTOBuilder();
        beerDTO.setQuantity(beerDTO.getQuantity()+quantityDTO.getQuantity());
        
        //when
        when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenReturn(beerDTO);
        
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())));
    }
    
    @Test
  void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
      QuantityDTO quantityDTO = QuantityDTO.builder()
              .quantity(30)
              .build();

      BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
      beerDTO.setQuantity(beerDTO.getQuantity() + quantityDTO.getQuantity());

      when(beerService.increment(VALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerStockExceededException.class);

      mockMvc.perform(patch(BEER_API_URL_PATH + "/" + VALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
  }

  @Test
  void whenPATCHIsCalledWithInvalidBeerIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
      QuantityDTO quantityDTO = QuantityDTO.builder()
              .quantity(30)
              .build();

      when(beerService.increment(INVALID_BEER_ID, quantityDTO.getQuantity())).thenThrow(BeerNotFoundException.class);
      mockMvc.perform(patch(BEER_API_URL_PATH + "/" + INVALID_BEER_ID + BEER_API_SUBPATH_INCREMENT_URL)
              .contentType(MediaType.APPLICATION_JSON)
              .content(asJsonString(quantityDTO)))
              .andExpect(status().isNotFound());
  }

 
    
    private BeerDTO beerDTOBuilder() {
		BeerDTO beerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
		return beerDTO;
	}

    
	
	

}
