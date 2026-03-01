package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.models.ProfileMastery;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.persistence.database.converters.MasteryConverter;
import jakarta.persistence.*;

import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "profile_mastery")
public class ProfileMasteryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "profile_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private ProfileEntity profile;

    @Convert(converter = MasteryConverter.class)
    @Column(name = "mastery_ID")
    private MasteryElement masteryElement;

    @Column(name = "level")
    private int level;

    @OneToMany(mappedBy = ProfileMasterySpeEntity_.PROFILE_MASTERY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Set<ProfileMasterySpeEntity> profileMasterySpeEntities = new HashSet<>();

    public ProfileMasteryEntity() {}

    public ProfileMasteryEntity(ProfileEntity profile) {
        this.profile = profile;
    }

    public Long getId() {
        return id;
    }

    public ProfileEntity getProfile() {
        return profile;
    }

    public MasteryElement getMasteryElement() {
        return masteryElement;
    }

    public int getLevel() {
        return level;
    }

    public Set<ProfileMasterySpeEntity> getProfileMasterySpeEntities() {
        return profileMasterySpeEntities;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() && ((ProfileMasteryEntity) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void syncFromDomain(ProfileMastery domain) {
        masteryElement = domain.getMasteryElement();
        level = domain.getLevel();

        syncMasterySpecializations(domain);
    }

    private void syncMasterySpecializations(ProfileMastery domain) {
        Set<MasterySpeElement> domainSpe = domain.getMasterySpecializations();

        profileMasterySpeEntities.removeIf(e -> !domainSpe.contains(e.getMasterySpecializationElement()));

        Set<MasterySpeElement> entitySpe = profileMasterySpeEntities.stream()
                .map(ProfileMasterySpeEntity::getMasterySpecializationElement)
                .collect(Collectors.toSet());

        for (MasterySpeElement elem : domainSpe) {
            if (entitySpe.contains(elem)) continue;

            ProfileMasterySpeEntity newSpe = new ProfileMasterySpeEntity(elem, this);
            profileMasterySpeEntities.add(newSpe);
        }
    }
}
