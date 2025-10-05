package com.archetype.layer.domain.model;

import java.util.List;

public record Species(int nationalId,
                      String name,
                      Type firstType,
                      Type secondType,
                      List<Ability> abilities,
                      List<EggGroup> eggGroups,
                      int baseHp,
                      List<Species> evolutions,
                      List<Tuple<Integer, Move>> moves) {


}

