package com.archetype.mvc.model;

import jakarta.annotation.Nonnull;

import java.util.List;

public record Type(Element element, List<Element> strongVs, List<Element> weakVs, List<Element> immuneTo) {

    public static final Type normal = new Type(
            Element.NORMAL,
            List.of(),
            List.of(Element.FIGHTING),
            List.of(Element.GHOST)
    );
    public static final Type fighting = new Type(
            Element.FIGHTING,
            List.of(Element.NORMAL, Element.ROCK, Element.STEEL, Element.ICE, Element.DARK),
            List.of(Element.FLYING, Element.PSYCHIC, Element.FAIRY),
            List.of()
    );
    public static final Type flying = new Type(
            Element.FLYING,
            List.of(Element.FIGHTING, Element.BUG, Element.GRASS),
            List.of(Element.ROCK, Element.ELECTRIC, Element.ICE),
            List.of(Element.GROUND)
    );
    public static final Type poison = new Type(
            Element.POISON,
            List.of(Element.GRASS, Element.FAIRY),
            List.of(Element.GROUND, Element.PSYCHIC),
            List.of()
    );
    public static final Type ground = new Type(
            Element.GROUND,
            List.of(Element.POISON, Element.ROCK, Element.STEEL, Element.FIRE, Element.ELECTRIC),
            List.of(Element.WATER, Element.GRASS, Element.ICE),
            List.of(Element.ELECTRIC)
    );
    public static final Type rock = new Type(
            Element.ROCK,
            List.of(Element.FIRE, Element.ICE, Element.FLYING, Element.BUG),
            List.of(Element.WATER, Element.GRASS, Element.FIGHTING, Element.GROUND, Element.STEEL),
            List.of()
    );
    public static final Type bug = new Type(
            Element.BUG,
            List.of(Element.GRASS, Element.PSYCHIC, Element.DARK),
            List.of(Element.FIRE, Element.FLYING, Element.ROCK),
            List.of()
    );
    public static final Type ghost = new Type(
            Element.GHOST,
            List.of(Element.GHOST, Element.PSYCHIC),
            List.of(Element.GHOST, Element.DARK),
            List.of(Element.NORMAL, Element.FIGHTING)
    );
    public static final Type fire = new Type(
            Element.FIRE,
            List.of(Element.GRASS, Element.ICE, Element.BUG, Element.STEEL),
            List.of(Element.WATER, Element.GROUND, Element.ROCK),
            List.of()
    );
    public static final Type water = new Type(
            Element.WATER,
            List.of(Element.FIRE, Element.GROUND, Element.ROCK),
            List.of(Element.ELECTRIC, Element.GRASS),
            List.of()
    );
    public static final Type grass = new Type(
            Element.GRASS,
            List.of(Element.WATER, Element.GROUND, Element.ROCK),
            List.of(Element.FIRE, Element.ICE, Element.POISON, Element.FLYING, Element.BUG),
            List.of()
    );
    public static final Type electric = new Type(
            Element.ELECTRIC,
            List.of(Element.WATER, Element.FLYING),
            List.of(Element.GROUND),
            List.of()
    );
    public static final Type psychic = new Type(
            Element.PSYCHIC,
            List.of(Element.FIGHTING, Element.POISON),
            List.of(Element.BUG, Element.GHOST, Element.DARK),
            List.of()
    );
    public static final Type ice = new Type(
            Element.ICE,
            List.of(Element.DRAGON, Element.GRASS, Element.GROUND, Element.FLYING),
            List.of(Element.FIRE, Element.FIGHTING, Element.ROCK, Element.STEEL),
            List.of()
    );
    public static final Type dragon = new Type(
            Element.DRAGON,
            List.of(Element.DRAGON),
            List.of(Element.ICE, Element.DRAGON, Element.FAIRY),
            List.of()
    );
    public static final Type dark = new Type(
            Element.DARK,
            List.of(Element.GHOST, Element.PSYCHIC),
            List.of(Element.FIGHTING, Element.BUG, Element.FAIRY),
            List.of(Element.PSYCHIC)
    );
    public static final Type fairy = new Type(
            Element.FAIRY,
            List.of(Element.DRAGON, Element.FIGHTING, Element.DARK),
            List.of(Element.POISON, Element.STEEL),
            List.of(Element.DRAGON)
    );
    public static final Type steel = new Type(
            Element.STEEL,
            List.of(Element.ICE, Element.ROCK, Element.FAIRY),
            List.of(Element.FIRE, Element.FIGHTING, Element.GROUND),
            List.of(Element.POISON)
    );
    public static final Type stellar = new Type(
            Element.STELLAR,
            List.of(),
            List.of(),
            List.of()
    );
    public static final Type unknown = new Type(
            Element.UNKNOWN,
            List.of(),
            List.of(),
            List.of()
    );

    public static Type fromElement(Element element) {
        return switch (element) {
            case NORMAL -> normal;
            case FIGHTING -> fighting;
            case FLYING -> flying;
            case POISON -> poison;
            case GROUND -> ground;
            case ROCK -> rock;
            case BUG -> bug;
            case GHOST -> ghost;
            case FIRE -> fire;
            case WATER -> water;
            case GRASS -> grass;
            case ELECTRIC -> electric;
            case PSYCHIC -> psychic;
            case ICE -> ice;
            case DRAGON -> dragon;
            case DARK -> dark;
            case FAIRY -> fairy;
            case STEEL -> steel;
            case STELLAR -> stellar;
            default -> unknown;
        };
    }

    @Override
    @Nonnull
    public String toString() {
        return element.toString();
    }
}
