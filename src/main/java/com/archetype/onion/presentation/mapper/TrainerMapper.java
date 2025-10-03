package com.archetype.onion.presentation.mapper;

import com.archetype.onion.domain.model.PokemonOwnership;
import com.archetype.onion.domain.model.Trainer;
import com.archetype.onion.presentation.dto.PokemonOwnershipDTO;
import com.archetype.onion.presentation.dto.TrainerDTO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for converting between domain models and DTOs.
 */
@Mapper(componentModel = "spring")
public interface TrainerMapper {
    
    /**
     * Convert Trainer domain model to DTO.
     */
    TrainerDTO toDTO(Trainer trainer);
    
    /**
     * Convert Trainer DTO to domain model.
     */
    Trainer toDomain(TrainerDTO dto);
    
    /**
     * Convert list of Trainer domain models to DTOs.
     */
    List<TrainerDTO> toDTOList(List<Trainer> trainers);
    
    /**
     * Convert PokemonOwnership domain model to DTO.
     */
    PokemonOwnershipDTO toDTO(PokemonOwnership ownership);
    
    /**
     * Convert PokemonOwnership DTO to domain model.
     */
    PokemonOwnership toDomain(PokemonOwnershipDTO dto);
}
