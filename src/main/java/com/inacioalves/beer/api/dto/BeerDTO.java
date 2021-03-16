package com.inacioalves.beer.api.dto;


import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Size;

import com.inacioalves.beer.api.enums.BeerType;
import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeerDTO {
	
	private Long id;
	
	@NotNull
	@Size(min=1,max=200)
	private String name;
	
	@NotNull
	@Size(min=1,max=200)
	private String brand;
	
	@NotNull
	@Max(500)
	private int max;
	
	@NotNull
	private int quantity;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private BeerType type;


}
