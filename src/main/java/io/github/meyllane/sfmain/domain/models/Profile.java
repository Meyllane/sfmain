package io.github.meyllane.sfmain.domain.models;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.domain.elements.SpeciesElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;

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

    public Profile(String name) {
        this.name = name;
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

    public void setProfileMastery(ProfileMastery profileMastery) {
        if (this.profileMastery != null && shouldRemoveMasterySpe(profileMastery)) {
            this.profileMastery.setMasterySpecializations(new HashSet<>());
        }

        this.profileMastery = profileMastery;
    }

    public void addMasterySpeElement(MasterySpeElement masterySpeElement) {
        if (!this.profileMastery.getMasteryElement().getSpecializations().contains(masterySpeElement)) {
            throw new SFException(ErrorMessage.get("profile.mastery_and_spe_incompatibility"));
        }

        if (!this.profileMastery.getMasterySpecializations().add(masterySpeElement)) {
            throw new SFException(ErrorMessage.get("profile.duplicate_spe"));
        }
    }

    public void removeMasterySpeElement(MasterySpeElement masterySpeElement) {
        if (!this.profileMastery.getMasterySpecializations().remove(masterySpeElement)) {
            throw new SFException(ErrorMessage.get("profile.missing_spe"));
        }
    }

    //Checks if we need to remove all previous specialization when changing the ProfileMastery
    private boolean shouldRemoveMasterySpe(ProfileMastery newProfileMastery) {
        return !newProfileMastery.getMasteryElement().equals(this.profileMastery.getMasteryElement());
    }

    public void addProfileTrait(ProfileTrait profileTrait) {
        if (!profileTraits.add(profileTrait)) {
            throw new SFException(ErrorMessage.get("profile.duplicate_trait"));
        }
    }

    public void removeProfileTrait(ProfileTrait profileTrait) {
        if (!profileTraits.remove(profileTrait)) {
            throw new SFException(ErrorMessage.get("profile.missing_trait"));
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
