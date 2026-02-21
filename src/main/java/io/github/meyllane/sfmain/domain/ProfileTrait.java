package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.elements.TraitElement;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileTraitEntity;

public class ProfileTrait {
    private TraitElement trait;
    private String specialization;

    public ProfileTrait(TraitElement trait, String specialization) {
        this.trait = trait;
        this.specialization = specialization;
    }

    public ProfileTrait(TraitElement trait) {
        this.trait = trait;
    }

    public TraitElement getTrait() {
        return trait;
    }

    public String getSpecialization() {
        return specialization;
    }

    public static ProfileTrait fromEntity(ProfileTraitEntity entity) {
        return new ProfileTrait(
                entity.getTrait(),
                entity.getSpecialization()
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
                ((ProfileTrait) obj).trait.equals(this.trait);
    }

    @Override
    public int hashCode() {
        return trait.hashCode();
    }
}
