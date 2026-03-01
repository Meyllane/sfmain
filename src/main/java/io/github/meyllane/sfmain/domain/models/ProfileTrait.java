package io.github.meyllane.sfmain.domain.models;

import io.github.meyllane.sfmain.domain.elements.TraitElement;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileTraitEntity;

public class ProfileTrait {
    private Long id;
    private final TraitElement trait;
    private String specialization;

    public ProfileTrait(TraitElement trait, String specialization) {
        this.trait = trait;
        this.specialization = specialization;
    }

    public ProfileTrait(Long id, TraitElement trait, String specialization) {
        this.id = id;
        this.trait = trait;
        this.specialization = specialization;
    }

    public TraitElement getTrait() {
        return trait;
    }

    public Long getId() {
        return id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public static ProfileTrait fromEntity(ProfileTraitEntity entity) {
        return new ProfileTrait(
                entity.getId(),
                entity.getTrait(),
                entity.getSpecialization()
        );
    }

    @Override
    public String toString() {
        return String.format("%s [%s]", this.trait.getName(), this.specialization);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
                ((ProfileTrait) obj).trait.equals(this.trait) && ((ProfileTrait) obj).specialization.equals(this.specialization);
    }

    @Override
    public int hashCode() {
        return trait.hashCode();
    }
}
