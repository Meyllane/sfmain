package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.ProfileTrait;
import io.github.meyllane.sfmain.persistence.database.converters.SpeciesConverter;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.elements.TraitElement;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "profile")
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "description")
    private String description;

    @Column(name = "species_ID")
    @Convert(converter = SpeciesConverter.class)
    private SpeciesElement speciesElement;

    @OneToMany(mappedBy = ProfileTraitEntity_.PROFILE, cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<ProfileTraitEntity> profileTraitEntities = new ArrayList<>();

    @OneToOne(mappedBy = ProfileMasteryEntity_.PROFILE)
    private ProfileMasteryEntity profileMastery;

    @JoinColumn(name = "user_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private UserEntity user;

    public ProfileEntity() {
    }

    public ProfileEntity(String profileName, SpeciesElement species) {
        name = profileName;
        speciesElement = species;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProfileTraitEntity> getProfileTraits() {
        return profileTraitEntities;
    }

    public List<String> getProfileTraitsName() {
        return profileTraitEntities.stream()
                .map(profileTraitEntity -> profileTraitEntity.getTrait().getName())
                .toList();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDescription() {
        return description;
    }

    public SpeciesElement getSpeciesElement() {
        return speciesElement;
    }

    public void setSpeciesElement(SpeciesElement speciesElement) {
        this.speciesElement = speciesElement;
    }

    public List<ProfileTraitEntity> getProfileTraitEntities() {
        return profileTraitEntities;
    }

    public ProfileMasteryEntity getProfileMastery() {
        return profileMastery;
    }

    public UserEntity getUserEntity() {
        return user;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((ProfileEntity) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void syncFromDomain(Profile domain) {
        name = domain.getName();
        age = domain.getAge();
        description = domain.getDescription();
        speciesElement = domain.getSpeciesElement();

        profileMastery.syncFromDomain(domain.getProfileMastery());

        syncProfileTrait(domain);
    }

    protected void syncProfileTrait(Profile domain) {
        Set<TraitElement> domainTrait = domain.getProfileTraits().stream()
                .map(ProfileTrait::getTrait)
                .collect(Collectors.toSet());

        //Removing ProfileTraitEntity if their Domain-equivalents were removed from the Domain
        profileTraitEntities.removeIf(p -> !domainTrait.contains(p.getTrait()));

        //Adding ProfileTraitEntity if their Domain-equivalents were added to the Domain
        Set<TraitElement> entityTrait = profileTraitEntities.stream()
                .map(ProfileTraitEntity::getTrait)
                .collect(Collectors.toSet());

        for (TraitElement trait : domainTrait) {
            if (entityTrait.contains(trait)) continue;

            ProfileTraitEntity entity = new ProfileTraitEntity(trait, this);
            profileTraitEntities.add(entity);
        }
    }
}
