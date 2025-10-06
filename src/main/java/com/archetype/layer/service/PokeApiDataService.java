package com.archetype.layer.service;

import com.archetype.layer.client.pokeapi.PokeApiClient;
import com.archetype.layer.client.pokeapi.dto.PokeApiPokemon;
import com.archetype.layer.domain.model.Ability;
import com.archetype.layer.domain.model.Element;
import com.archetype.layer.domain.model.Species;
import com.archetype.layer.domain.model.Type;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for populating the database with Pokemon data from PokeAPI.
 * Fetches the first 151 Pokemon from PokeAPI and saves them to the MongoDB database.
 * Follows ADR 0007 (Prefer OpenFeign) and ADR 0002 (Domain separation and mapping).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PokeApiDataService {

    final PokeApiClient client;

    public List<Species> getFirstGenerationSpecies() {

        List<Species> firstGen = new ArrayList<>();
        for (int i = 1; i <= 151; i++) {
            PokeApiPokemon pokemon = client.getPokemonById(i);
            firstGen.add(toSpecies(pokemon));
        }
        return firstGen;
    }

    private Species toSpecies(PokeApiPokemon pokemon) {

        Type firstType = Type.fromElement(Element.valueOf(pokemon.types().getFirst().type().name().toUpperCase()));
        Type secondType = pokemon.types().size() > 1 ? Type.fromElement(Element.valueOf(pokemon.types().get(1).type().name().toUpperCase())) : null;

        List<Ability> abilities1 = pokemon.abilities()
                                          .stream()
                                          .map(ab -> getAbilityByName(ab.ability().name(), ab.isHidden()))
                                          .toList();


        var intermediate = pokemon.moves().stream()
                                  .filter(PokeApiPokemon.PokeApiMove::isValid).toList();

        var medio = intermediate.stream()
                                .filter(m -> m.move().number() > 0 && m.move().name() != null && !m.move().name().isBlank())
                                .map(PokeApiPokemon.PokeApiMove::move)
                                .toList();


        Map<Integer, String> moves = medio.stream().collect(Collectors.toMap(
                PokeApiPokemon.PokeApiMove.PokeApiMoveId::number,
                PokeApiPokemon.PokeApiMove.PokeApiMoveId::name,
                (k, v) -> k,
                HashMap::new
        ));

        Species.PokemonStats stats = new Species.PokemonStats(
                pokemon.stat("attack"),
                pokemon.stat("defense"),
                pokemon.stat("special-attack"),
                pokemon.stat("special-defense"),
                pokemon.stat("speed"),
                pokemon.stat("hp")
        );


        return new Species(
                pokemon.id(),
                pokemon.name(),
                firstType,
                secondType,
                abilities1,
                moves,
                stats
        );
    }

    private Ability getAbilityByName(String name, boolean hidden) {
        var ab = client.getAbilityByName(name);
        return new Ability(ab.id(), ab.name(), ab.effects().stream()
                                                 .filter(a -> "en".equals(a.language().name()))
                                                 .findFirst()
                                                 .map(x -> x.language().name()).stream().findFirst().orElseGet(() -> ""),
                hidden);

    }


}
