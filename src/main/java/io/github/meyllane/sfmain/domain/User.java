package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class User {
    private Long id;
    private UUID minecraftUUID;
    private String minecraftName;
    private Profile activeProfile;
    private Set<Profile> profiles;

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
            Profile activeProfile = Profile.fromEntity(entity.getActiveProfile());
            activeProfile.setUser(user);
            user.setActiveProfile(activeProfile);
        }

        // Profiles collection
        Set<Profile> profiles = entity.getProfiles() == null
                ? new HashSet<>()
                : entity.getProfiles().stream()
                .map(Profile::fromEntity)
                .collect(Collectors.toSet());

        profiles.forEach(profile -> profile.setUser(user));

        user.setProfiles(profiles);

        return user;
    }
}
