package com.archetype.layer.domain.model;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;


public class Pokemon {

    public static Random rand = new Random();
    final int attackIV;
    final int defenseIV;
    final int speedIV;
    final int specialIV;
    final int hpIV;
    final boolean shiny;
    Species species;
    String name;
    int level;
    long maxHp;
    MoveSet moveSet = new MoveSet();

    public Pokemon(Species species, String name, int level) {

        this.name = StringUtils.hasText(name) ? name : species.name();

        this.species = species;

        this.level = level;

        this.attackIV = rand.nextInt(16);// 4-bit random value (0-15)
        this.defenseIV = rand.nextInt(16);
        this.speedIV = rand.nextInt(16);
        this.specialIV = rand.nextInt(16);
        this.hpIV = calculateHpIV();
        this.shiny = calculateShiny();
        this.maxHp = calculateMaxHp();
        species.moves().stream()
               .filter(t -> t.first() == 1)
               .forEach(m -> this.moveSet.addMove(m.second()));
    }

    boolean calculateShiny() {
        // Shiny calculation based on Generation 2 IVs
        return (defenseIV == 10 && speedIV == 10 && specialIV == 10) &&
                (List.of(2, 3, 6, 7, 11, 14, 15).contains(attackIV));
    }

    int calculateHpIV() {
        return (attackIV % 2) * 8 + (defenseIV % 2) * 4 + (speedIV % 2) * 2 + (specialIV % 2);
    }

    int calculateMaxHp() {
        return ((this.species.baseHp() + this.hpIV) * this.level / 100) + this.level + 10;
    }

    // Public accessors for mapping and external consumers
    public Species getSpecies() {
        return this.species;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public boolean isShiny() {
        return this.shiny;
    }

    public MoveSet getMoveSet() {
        return this.moveSet;
    }

    @Data
    public static
    class MoveSet {

        Move move1;
        Move move2;
        Move move3;
        Move move4;

        public void addMove(Move move) {
            if (move == null) return;
            if (move.equals(move1) || move.equals(move2) ||
                    move.equals(move3) || move.equals(move4)) {
                return;
            }
            if (replaceMove(move1, move).equals(move)) return;
            if (replaceMove(move2, move).equals(move)) return;
            if (replaceMove(move3, move).equals(move)) return;
            replaceMove(move4, move);
        }

        Move replaceMove(Move initial, Move replacement) {
            if (initial.hm()) return initial;
            return replacement;
        }

        public boolean setMove(Move move, int slot) {
            if (move == null) return false;
            return switch (slot) {
                case 1 -> replaceMove(move1, move).equals(move);
                case 2 -> replaceMove(move2, move).equals(move);
                case 3 -> replaceMove(move3, move).equals(move);
                case 4 -> replaceMove(move4, move).equals(move);
                default -> false;
            };
        }
    }
}

