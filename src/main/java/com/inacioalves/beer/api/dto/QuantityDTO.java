package com.inacioalves.beer.api.dto;

import javax.validation.constraints.Max;

import com.sun.istack.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {
	
	@NotNull
	@Max(100)
	private Integer quantity;

}
