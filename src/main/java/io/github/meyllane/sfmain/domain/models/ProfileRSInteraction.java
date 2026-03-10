package io.github.meyllane.sfmain.domain.models;

import io.github.meyllane.sfmain.persistence.database.entities.ProfileRSInteractionEntity;

import java.time.LocalDateTime;

public class ProfileRSInteraction {
    private final int resourceSpotID;
    private int nbInteraction;
    private LocalDateTime lastInteractionDate;

    public ProfileRSInteraction(int resourceSpotID, int nbInteraction, LocalDateTime lastInteractionDate) {
        this.resourceSpotID = resourceSpotID;
        this.nbInteraction = nbInteraction;
        this.lastInteractionDate = lastInteractionDate;
    }

    public int getNbInteraction() {
        return nbInteraction;
    }

    public void setNbInteraction(int nbInteraction) {
        this.nbInteraction = nbInteraction;
    }

    public LocalDateTime getLastInteractionDate() {
        return lastInteractionDate;
    }

    public void setLastInteractionDate(LocalDateTime lastInteractionDate) {
        this.lastInteractionDate = lastInteractionDate;
    }

    public int getResourceSpotID() {
        return resourceSpotID;
    }

    public void increaseInteractionCount() {
        this.nbInteraction++;
    }

    public static ProfileRSInteraction fromEntity(ProfileRSInteractionEntity entity) {
        return new ProfileRSInteraction(
                entity.getResourceSpotID(),
                entity.getNbInteraction(),
                entity.getLastInteractionDate()
        );
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ProfileRSInteraction prs
                && prs.getResourceSpotID() == this.getResourceSpotID();
    }

    @Override
    public int hashCode() {
        return this.resourceSpotID;
    }
}
