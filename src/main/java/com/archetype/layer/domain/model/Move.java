package com.archetype.layer.domain.model;

import io.github.darkona.logged.utils.Transformer;

public record Move(Integer number, String name, Element element, Integer power, Integer pp, boolean hm) {

    public enum Type {
        PHYSICAL, SPECIAL, STATUS, UNKNOWN;

        public String normalized() {
            return Transformer.capitalize(name());
        }
    }
}

