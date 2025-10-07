package com.archetype.layer.domain.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public record Species(int nationalId,
                      String name,
                      Type firstType,
                      Type secondType,
                      List<Ability> abilities,
                      Map<Integer, String> moves,
                      PokemonStats stats
) {

    public List<String> typesAsString() {
        List<String> types = new ArrayList<>();
        types.add(firstType.element().name());
        if(secondType != null) {
            types.add(secondType.element().name());
        }
        return types;
    }

    public record PokemonStats(int attack, int defense, int specialAttack, int specialDefense, int speed, int hp) {
    }
}

