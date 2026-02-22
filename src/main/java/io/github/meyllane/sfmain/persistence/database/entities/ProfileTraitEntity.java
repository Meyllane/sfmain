package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.persistence.database.converters.TraitConverter;
import io.github.meyllane.sfmain.elements.TraitElement;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "profile_trait")
public class ProfileTraitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity profile;

    @Column(name = "trait_ID")
    @Convert(converter = TraitConverter.class)
    private TraitElement trait;

    @Column(name = "trait_specialization")
    private String specialization;

    public ProfileTraitEntity() {}

    public ProfileTraitEntity(TraitElement trait, ProfileEntity profile) {
        this.trait = trait;
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public TraitElement getTrait() {
        return trait;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTrait(TraitElement trait) {
        this.trait = trait;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((ProfileTraitEntity) obj).trait.equals(trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trait);
    }
}
