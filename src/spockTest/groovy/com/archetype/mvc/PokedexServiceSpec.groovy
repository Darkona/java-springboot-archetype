package com.archetype.mvc

import com.archetype.mvc.persistence.PokemonRepository
import com.archetype.mvc.persistence.document.PokemonDocument
import com.archetype.mvc.service.PokedexService
import spock.lang.Specification

class PokedexServiceSpec extends Specification {

    PokemonRepository repository = Mock()
    PokedexService service = new PokedexService(repository)

    def "findAllSpecies returns mapped overviews"() {
        given:
        UUID id = UUID.randomUUID()
        PokemonDocument doc = new PokemonDocument()
        doc.setId(id)
        doc.setName("Bulbasaur")
        doc.setTypes(List.of("Grass", "Poison"))

        repository.findAll() >> [doc]

        when:
        def result = service.findAllSpecies()

        then:
        result.size() == 1
        result[0].id() == id
        result[0].name() == "Bulbasaur"
        result[0].types() == ["Grass", "Poison"]
    }

    def "findSpeciesById returns empty when not found"() {
        given:
        UUID id = UUID.randomUUID()
        repository.findById(id) >> Optional.empty()

        when:
        def opt = service.findSpeciesById(id)

        then:
        !opt.isPresent()
    }
}

