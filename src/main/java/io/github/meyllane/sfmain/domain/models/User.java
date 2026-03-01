package io.github.meyllane.sfmain.domain.models;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;

import java.util.HashSet;
import java.util.Set;

public class User {
    private Long id;
    private String minecraftUUID;
    private String minecraftName;
    private Profile activeProfile;
    private Set<Profile> profiles = new HashSet<>();

    public User(Long id, String minecraftUUID, String minecraftName) {
        this.id = id;
        this.minecraftUUID = minecraftUUID;
        this.minecraftName = minecraftName;
    }

    public User(String minecraftUUID, String minecraftName) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftName = minecraftName;
    }

    public Long getId() {
        return id;
    }

    public String getMinecraftUUID() {
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
        activeProfile.setUser(this);
    }

    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }

    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }

    public void addProfile(Profile profile) {
        if (!this.profiles.add(profile)) {
            throw new SFException(ErrorMessage.get("user.duplicate_profile"));
        }

        profile.setUser(this);
    }

    public void removeProfile(Profile profile) {
        if (!this.profiles.remove(profile)) {
            throw new SFException(ErrorMessage.get("user.missing_profile"));
        }

        profile.setUser(null);
    }

    public static User fromEntity(UserEntity entity) {
        User user = new User(
                entity.getId(),
                entity.getMinecraftUUID(),
                entity.getMinecraftName()
        );

        // Active profile
        if (entity.getActiveProfile() != null) {
            Profile activeProfile = SFMain.profileRegistry.get(entity.getActiveProfile().getName());
            activeProfile.setUser(user);
            user.setActiveProfile(activeProfile);
        }

        //Owned profiles
        for (ProfileEntity profileEntity : entity.getProfiles()) {
            Profile profile = SFMain.profileRegistry.get(profileEntity.getName());
            profile.setUser(user);
            user.profiles.add(profile);
        }

        return user;
    }
}
