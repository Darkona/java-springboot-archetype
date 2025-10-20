package com.archetype.layer.mapper.dto;

import com.archetype.layer.domain.dto.response.SpeciesResponse;
import com.archetype.layer.domain.model.Species;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface SpeciesDtoMapper {

    SpeciesResponse toDto(Species species);

    List<SpeciesResponse> toDto(List<Species> species);
}
