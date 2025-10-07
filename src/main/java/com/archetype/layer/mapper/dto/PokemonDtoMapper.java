package com.archetype.layer.mapper.dto;

import com.archetype.layer.domain.dto.request.PokemonCreate;
import com.archetype.layer.domain.model.Pokemon;
import org.mapstruct.Mapper;

@Mapper
public interface PokemonDtoMapper {

    Pokemon toDomain(PokemonCreate pokemonCreateRequest);
}
