package com.archetype.mvc.controller;

import com.archetype.mvc.model.SpeciesOverview;
import com.archetype.mvc.service.PokedexService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Simple MVC controller for the read-only Pokedex.
 */
@Controller
@RequestMapping("/pokedex")
public class PokedexController {

    private final PokedexService service;

    public PokedexController(PokedexService service) {
        this.service = service;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pokemons", service.findAllSpecies());
        return "pokedex/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") int id, Model model) {
        SpeciesOverview overview = service.findSpeciesById(id);
        model.addAttribute("pokemon", overview);
        return "pokedex/detail";
    }
}

