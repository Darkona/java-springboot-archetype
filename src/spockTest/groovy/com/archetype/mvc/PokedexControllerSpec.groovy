package com.archetype.mvc

import com.archetype.mvc.controller.PokedexController
import com.archetype.mvc.model.SpeciesOverview
import com.archetype.mvc.service.PokedexService
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Unroll

import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(controllers = PokedexController)
class PokedexControllerSpec extends Specification {

    @Autowired
    MockMvc mvc

    @MockBean
    PokedexService service

    def "GET /pokedex returns list view with pokemons in model"() {
        given:
        def id = UUID.randomUUID()
        def overview = new SpeciesOverview(id, "Bulbasaur", List.of("Grass", "Poison"))
        given(service.findAllSpecies()).willReturn([overview])

        when:
        def result = mvc.perform(get("/pokedex"))

        then:
        result.andExpect(status().isOk())
              .andExpect(view().name("pokedex/list"))
              .andExpect(model().attributeExists("pokemons"))
              .andExpect(model().attribute("pokemons", [overview]))
    }

    @Unroll
    def "GET /pokedex/{id} returns detail view when species exists"() {
        given:
        def id = UUID.randomUUID()
        def overview = new SpeciesOverview(id, "Charmander", List.of("Fire"))
        given(service.findSpeciesById(id)).willReturn(Optional.of(overview))

        when:
        def result = mvc.perform(get("/pokedex/${id}"))

        then:
        result.andExpect(status().isOk())
              .andExpect(view().name("pokedex/detail"))
              .andExpect(model().attributeExists("pokemon"))
              .andExpect(model().attribute("pokemon", overview))

        where:
        // single iteration, kept for didactic @Unroll example
        _ << [0]
    }

    def "GET /pokedex/{id} returns 404 when species not found"() {
        given:
        def id = UUID.randomUUID()
        given(service.findSpeciesById(id)).willReturn(Optional.empty())

        when:
        def result = mvc.perform(get("/pokedex/${id}"))

        then:
        result.andExpect(status().isNotFound())
    }
}

