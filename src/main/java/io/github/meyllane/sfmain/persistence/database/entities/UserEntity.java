package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.models.User;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.*;

@Entity
@Table(name = "plugin_user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "minecraft_UUID", unique = true)
    private String minecraftUUID;

    @Column(name = "minecraft_name")
    private String minecraftName;

    @OneToMany(mappedBy = ProfileEntity_.USER, cascade = {CascadeType.PERSIST})
    private Set<ProfileEntity> profiles = new HashSet<>();

    @JoinColumn(name = "active_profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity activeProfile;

    public UserEntity() {}

    public UserEntity(String minecraftUUID, String minecraftName) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftName = minecraftName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getMinecraftUUID() {
        return minecraftUUID;
    }

    public void setMinecraftUUID(String minecraftUUID) {
        this.minecraftUUID = minecraftUUID;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public void setMinecraftName(String minecraftName) {
        this.minecraftName = minecraftName;
    }

    public ProfileEntity getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(ProfileEntity activeProfile) {
        this.activeProfile = activeProfile;
    }

    public Collection<ProfileEntity> getProfiles() {
        return profiles;
    }

    public void addProfile(ProfileEntity profile) {
        profiles.add(profile);
        profile.setUser(this);
    }

    public void removeProfile(ProfileEntity profile) {
        profiles.removeIf(profileEntity -> profileEntity.equals(profile));
        profile.setUser(null);
    }

    public void syncFromDomain(User domain) {
        this.minecraftUUID = domain.getMinecraftUUID();
        this.minecraftName = domain.getMinecraftName();
    }
}
