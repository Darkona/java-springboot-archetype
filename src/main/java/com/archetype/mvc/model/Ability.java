package com.archetype.mvc.model;

import io.github.darkona.logged.utils.Transformer;

public record Ability(int id, String name, String description, Boolean hidden) {

    public Ability(int id, String name, String description, Boolean hidden) {
        this.id = id;
        this.name = Transformer.capitalize(name);
        this.description = description;
        this.hidden = hidden;
    }
}

