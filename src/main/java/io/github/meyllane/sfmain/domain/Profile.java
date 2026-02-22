package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.elements.TraitElement;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileTraitEntity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Profile {
    private Long id;
    private String name;
    private int age;
    private String description;
    private SpeciesElement speciesElement;
    private Set<ProfileTrait> profileTraits = new HashSet<>();
    private ProfileMastery profileMastery;
    private User user;

    public Profile(Long id, String name, int age, String description, SpeciesElement speciesElement) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.description = description;
        this.speciesElement = speciesElement;
    }

    public Profile(String name, SpeciesElement speciesElement)  {
        this.name = name;
        this.speciesElement = speciesElement;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public SpeciesElement getSpeciesElement() {
        return speciesElement;
    }

    public Set<ProfileTrait> getProfileTraits() {
        return profileTraits;
    }

    public ProfileMastery getProfileMastery() {
        return profileMastery;
    }

    public User getUser() {
        return user;
    }

    public List<String> getProfileTraitsName() {
        return this.getProfileTraits().stream()
                .map(p -> p.getTrait().getName())
                .toList();
    }

    private void setProfileTraits(Set<ProfileTrait> traits) {
        profileTraits = traits;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpeciesElement(SpeciesElement speciesElement) {
        this.speciesElement = speciesElement;
    }

    public void addProfileTrait(ProfileTrait profileTrait) {
        if (!profileTraits.add(profileTrait)) {
            throw new SFException("Ce profile a déjà le trait " + profileTrait.getTrait().getName() + ".");
        }
    }

    public void removeProfileTrait(ProfileTrait profileTrait) {
        if (!profileTraits.remove(profileTrait)) {
            throw new SFException("Ce profile ne posséde pas le trait " + profileTrait.getTrait().getName() + ".");
        }
    }

    public void updateProfileTrait(ProfileTrait profileTrait) {
        for (ProfileTrait p : profileTraits) {
            if (!p.equals(profileTrait)) continue;

            p.setSpecialization(profileTrait.getSpecialization());
        }
    }

    public static Profile fromEntity(ProfileEntity entity) {
        Profile profile = new Profile(
                entity.getId(),
                entity.getName(),
                entity.getAge(),
                entity.getDescription(),
                entity.getSpeciesElement()
        );

        profile.setProfileTraits(entity.getProfileTraitEntities().stream()
                .map(ProfileTrait::fromEntity)
                .collect(Collectors.toSet())
        );

        if (entity.getProfileMastery() != null)
            profile.profileMastery = ProfileMastery.fromEntity(entity.getProfileMastery());

        return profile;
    }
}
