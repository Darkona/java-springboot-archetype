package com.archetype.layer.persistence.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.UUID;
import java.util.List;

@Document(collection = "pokemons")
public class PokemonDocument {
    
    @Id
    private UUID id;
    private String name;
    private List<String> types;
    private List<String> abilities;
    private Integer baseExperience;
    private Integer height;
    private Integer weight;
    
    // Getters and setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<String> getTypes() {
        return types;
    }
    
    public void setTypes(List<String> types) {
        this.types = types;
    }
    
    public List<String> getAbilities() {
        return abilities;
    }
    
    public void setAbilities(List<String> abilities) {
        this.abilities = abilities;
    }
    
    public Integer getBaseExperience() {
        return baseExperience;
    }
    
    public void setBaseExperience(Integer baseExperience) {
        this.baseExperience = baseExperience;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public Integer getWeight() {
        return weight;
    }
    
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
}

