package com.archetype.layer.domain.model;

import java.util.List;

public record Type(Element element, List<Element> strongAgainst, List<Element> weakAgainst
        , List<Element> resistantTo, List<Element> vulnerableTo) {

    public boolean isStrongAgainst(Element type) {
        return strongAgainst.contains(type);
    }

    public boolean isWeakAgainst(Element type) {
        return weakAgainst.contains(type);
    }

    public boolean isVulnerableTo(Element type) {
        return vulnerableTo.contains(type);
    }

    public boolean isResistantTo(Element type) {
        return resistantTo.contains(type);
    }
}

