package com.archetype.mvc

import com.archetype.mvc.exception.PokemonNotFoundException
import com.archetype.mvc.model.Species
import com.archetype.mvc.model.Type
import com.archetype.mvc.persistence.PokemonMvcDataRepository
import com.archetype.mvc.persistence.document.SpeciesDocument
import com.archetype.mvc.service.PokedexService
import spock.lang.Specification

class PokedexServiceSpec extends Specification {

    PokemonMvcDataRepository repository = Mock()
    PokedexService service = new PokedexService(repository)

    def "findAllSpecies returns mapped overviews"() {
        given:
        Species doc = new Species(1,
                "Bulbasaur",
                Type.grass,
                Type.poison,
                Collections.emptyList(),
                Map.of(),
                new Species.PokemonStats(0,0,0,0,0,0) );

        repository.getAllSpecies() >> [doc]

        when:
        def result = service.findAllSpecies()

        then:
        result.size() == 1
        result[0].id() == 1
        result[0].name() == "Bulbasaur"
        result[0].types() == ["GRASS", "POISON"]
    }

    def "findSpeciesById throws exception when not found"() {
        given:
        def id = -1

        repository.getSpeciesById(id) >> { throw new PokemonNotFoundException(id) }

        when:
        service.findSpeciesById(id)

        then:
        thrown(PokemonNotFoundException)
    }
}
