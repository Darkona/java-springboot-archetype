package com.archetype.layer.domain.model;

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

    public record PokemonStats(int attack, int defense, int specialAttack, int specialDefense, int speed, int hp) {
    }
}

