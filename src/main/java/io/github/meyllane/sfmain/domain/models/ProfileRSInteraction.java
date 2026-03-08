package io.github.meyllane.sfmain.domain.models;

import io.github.meyllane.sfmain.persistence.database.entities.ProfileRSInteractionEntity;

import java.time.LocalDateTime;

public class ProfileRSInteraction {
    private final int resourceSpotID;
    private int nbInteraction;
    private LocalDateTime lastInteractionDate;

    public ProfileRSInteraction(int resourceSpotID, int nbInteraction, LocalDateTime last_interaction_date) {
        this.resourceSpotID = resourceSpotID;
        this.nbInteraction = nbInteraction;
        this.lastInteractionDate = last_interaction_date;
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

    public void setLast_interaction_date(LocalDateTime last_interaction_date) {
        this.lastInteractionDate = last_interaction_date;
    }

    public int getResourceSpotID() {
        return resourceSpotID;
    }

    public static ProfileRSInteraction fromEntity(ProfileRSInteractionEntity entity) {
        return new ProfileRSInteraction(
                entity.getResourceSpotID(),
                entity.getNbInteraction(),
                entity.getLastInteractionDate()
        );
    }
}
