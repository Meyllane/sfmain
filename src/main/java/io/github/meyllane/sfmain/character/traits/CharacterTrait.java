package io.github.meyllane.sfmain.character.traits;

public class CharacterTrait {
    private final Trait trait;
    private final String specialization;

    public CharacterTrait(Trait trait, String specialization) {
        this.trait = trait;
        this.specialization = specialization;
    }

    public Trait getTrait() {
        return trait;
    }

    public String getSpecialization() {
        return specialization;
    }
}
