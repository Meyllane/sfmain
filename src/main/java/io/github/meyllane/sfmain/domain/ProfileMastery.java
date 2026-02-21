package io.github.meyllane.sfmain.domain;

import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileMasteryEntity;

public class ProfileMastery {
    private MasteryElement masteryElement;
    private int level;

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

    public static ProfileMastery fromEntity(ProfileMasteryEntity entity) {
        return new ProfileMastery(
                entity.getMasteryElement(),
                entity.getLevel()
        );
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
