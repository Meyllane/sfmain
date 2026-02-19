package io.github.meyllane.sfmain.database.entities;

import io.github.meyllane.sfmain.database.converters.UUIDStringConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NaturalId
    @Column(name = "minecraft_UUID", unique = true)
    @Convert(converter = UUIDStringConverter.class)
    private UUID minecraftUUID;

    @Column(name = "minecraft_name")
    private String minecraftName;

    @OneToMany(mappedBy = Profile_.USER)
    private Collection<Profile> profiles = new ArrayList<>();

    @JoinColumn(name = "active_profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Profile activeProfile;

    public User() {}

    public User(UUID minecraftUUID) {
        this.minecraftUUID = minecraftUUID;
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

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public void setActiveProfile(Profile activeProfile) {
        this.activeProfile = activeProfile;
    }
}
