package com.archetype.layer.domain.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Random;


@Setter
@Getter
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
        species.moves().entrySet().stream()
               .filter(t -> t.getKey() == 1)
               .forEach(m -> this.moveSet.addMove(m.getValue()));
    }

    public List<String> getMoves() {
        return List.of(moveSet.move1, moveSet.move2, moveSet.move3, moveSet.move4);
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
        return ((this.species.stats().hp() + this.hpIV) * this.level / 100) + this.level + 10;
    }

    @Data
    public static
    class MoveSet {

        String move1;
        String move2;
        String move3;
        String move4;

        public void addMove(String move) {
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

        String replaceMove(String initial, String replacement) {
            //if (initial.hm()) return initial;
            return replacement;
        }

    }
}

