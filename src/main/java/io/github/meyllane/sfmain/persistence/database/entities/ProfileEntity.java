package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.ProfileMastery;
import io.github.meyllane.sfmain.domain.ProfileTrait;
import io.github.meyllane.sfmain.persistence.database.converters.SpeciesConverter;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.elements.TraitElement;
import jakarta.persistence.*;

import java.util.*;
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
    private Set<ProfileTraitEntity> profileTraitEntities = new HashSet<>();

    @OneToOne(mappedBy = ProfileMasteryEntity_.PROFILE, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
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

    public Set<ProfileTraitEntity> getProfileTraits() {
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

    public Set<ProfileTraitEntity> getProfileTraitEntities() {
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

        if (profileMastery == null && domain.getProfileMastery() != null) {
            this.profileMastery = new ProfileMasteryEntity(this);
        }

        if (profileMastery != null) profileMastery.syncFromDomain(domain.getProfileMastery());

        syncProfileTrait(domain);
    }

    protected void syncProfileTrait(Profile domain) {
        Map<TraitElement, ProfileTrait> domainTraitMap = domain.getProfileTraits().stream()
                .collect(Collectors.toMap(ProfileTrait::getTrait, p -> p));

        Map<TraitElement, ProfileTraitEntity> entityTraitMap = profileTraitEntities.stream()
                .collect(Collectors.toMap(ProfileTraitEntity::getTrait, p -> p));

        // Removing ProfileTraitEntity if their Domain-equivalents were removed from the Domain
        profileTraitEntities.removeIf(p -> !domainTraitMap.containsKey(p.getTrait()));

        for (Map.Entry<TraitElement, ProfileTrait> entry : domainTraitMap.entrySet()) {
            ProfileTraitEntity entity = entityTraitMap.get(entry.getKey());

            if (entity == null) {
                // Adding ProfileTraitEntity if their Domain-equivalents were added to the Domain
                profileTraitEntities.add(new ProfileTraitEntity(entry.getKey(), this));
            } else if (!entry.getValue().getSpecialization().equals(entity.getSpecialization())) {
                // Syncing specialization
                entity.setSpecialization(entry.getValue().getSpecialization());
            }
        }
    }
}
