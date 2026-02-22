package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.persistence.database.converters.UUIDStringConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.*;

@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "minecraft_UUID", unique = true)
    @Convert(converter = UUIDStringConverter.class)
    private UUID minecraftUUID;

    @Column(name = "minecraft_name")
    private String minecraftName;

    @OneToMany(mappedBy = ProfileEntity_.USER)
    private Set<ProfileEntity> profiles = new HashSet<>();

    @JoinColumn(name = "active_profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity activeProfile;

    public UserEntity() {}

    public UserEntity(UUID minecraftUUID, String minecraftName) {
        this.minecraftUUID = minecraftUUID;
        this.minecraftName = minecraftName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public UUID getMinecraftUUID() {
        return minecraftUUID;
    }

    public void setMinecraftUUID(UUID minecraftUUID) {
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
}
