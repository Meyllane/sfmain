package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileMasteryEntity;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileMasterySpeEntity;

import java.util.HashSet;
import java.util.Set;

public class ProfileMastery {
    private MasteryElement masteryElement;
    private int level;
    private Set<MasterySpeElement> masterySpecializations = new HashSet<>();

    public ProfileMastery(MasteryElement masteryElement, int level) {
        this.masteryElement = masteryElement;
        this.level = level;
    }

    public MasteryElement getMasteryElement() {
        return masteryElement;
    }

    public int getLevel() {
        return level;
    }

    public Set<MasterySpeElement> getMasterySpecializations() {
        return masterySpecializations;
    }

    public void setMasterySpecializations(Set<MasterySpeElement> masterySpecializations) {
        this.masterySpecializations = masterySpecializations;
    }

    public static ProfileMastery fromEntity(ProfileMasteryEntity entity) {
        ProfileMastery profileMastery = new ProfileMastery(
                entity.getMasteryElement(),
                entity.getLevel()
        );

        for (ProfileMasterySpeEntity speEntity : entity.getProfileMasterySpeEntities()) {
            profileMastery.masterySpecializations.add(speEntity.getMasterySpecializationElement());
        }

        return profileMastery;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() && ((ProfileMastery) obj).masteryElement.equals(masteryElement);
    }

    @Override
    public int hashCode() {
        return masteryElement.hashCode();
    }
}
