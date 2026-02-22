package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class User {
    private static final ProfileService profileService = SFMain.profileService;

    private Long id;
    private UUID minecraftUUID;
    private String minecraftName;
    private Profile activeProfile;
    private Set<Profile> profiles = new HashSet<>();

    public User(Long id, UUID minecraftUUID, String minecraftName) {
        this.id = id;
        this.minecraftUUID = minecraftUUID;
        this.minecraftName = minecraftName;
    }

    public Long getId() {
        return id;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public Set<Profile> getProfiles() {
        return profiles;
    }

    public void setActiveProfile(Profile activeProfile) {
        this.activeProfile = activeProfile;
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public static User fromEntity(UserEntity entity) {
        User user = new User(
                entity.getId(),
                entity.getMinecraftUUID(),
                entity.getMinecraftName()
        );

        // Active profile
        if (entity.getActiveProfile() != null) {
            Profile activeProfile = profileService.getProfile(entity.getActiveProfile().getName());
            activeProfile.setUser(user);
            user.setActiveProfile(activeProfile);
        }

        //Owned profiles
        for (ProfileEntity profileEntity : entity.getProfiles()) {
            Profile profile = profileService.getProfile(profileEntity.getName());
            profile.setUser(user);
            user.profiles.add(profile);
        }

        return user;
    }
}
