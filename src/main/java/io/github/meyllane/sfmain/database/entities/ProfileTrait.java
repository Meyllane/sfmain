package io.github.meyllane.sfmain.database.entities;

import io.github.meyllane.sfmain.database.converters.TraitConverter;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "profile_trait")
public class ProfileTrait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @Column(name = "trait_ID")
    @Convert(converter = TraitConverter.class)
    private TraitElement trait;

    @Column(name = "trait_specialization")
    private String specialization;

    public ProfileTrait() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public TraitElement getTrait() {
        return trait;
    }

    public void setTrait(TraitElement trait) {
        this.trait = trait;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((ProfileTrait) obj).trait.equals(trait);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, trait);
    }
}
