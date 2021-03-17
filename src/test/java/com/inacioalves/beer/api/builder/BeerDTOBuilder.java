package com.inacioalves.beer.api.builder;

import com.inacioalves.beer.api.dto.BeerDTO;
import com.inacioalves.beer.api.enums.BeerType;

import lombok.Builder;

@Builder
public class BeerDTOBuilder {

		
		@Builder.Default
		private Long id =1L;
		
		@Builder.Default
		private String name ="Smirnoff";
		
		@Builder.Default
		private String brand ="Bacardi";
		
		@Builder.Default
		private int max =50;
		
		@Builder.Default
		private int quantity=10;
		
		@Builder.Default
		private BeerType type =BeerType.ALE;
		
		public BeerDTO toBeerDTO() {
			return new BeerDTO(
					id,
					name,
					brand, 
					max,
					quantity,
					type);
					
		}
	
}
