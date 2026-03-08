package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.models.ProfileRSInteraction;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity()
@Table(name = "profile_resource_spot_interaction")
public class ProfileRSInteractionEntity {
    @Id
    private Long id;

    public ProfileRSInteractionEntity() {
    }

    public ProfileRSInteractionEntity(ProfileEntity profile, int resourceSpotID, int nbInteraction, LocalDateTime lastInteractionDate) {
        this.profile = profile;
        this.resourceSpotID = resourceSpotID;
        this.nbInteraction = nbInteraction;
        this.lastInteractionDate = lastInteractionDate;
    }

    @JoinColumn(name = "profile_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileEntity profile;

    @Column(name = "resource_spot_ID")
    private int resourceSpotID;

    @Column(name = "nb_interaction")
    private int nbInteraction;

    @Column(name = "last_interaction_date")
    private LocalDateTime lastInteractionDate;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public int getResourceSpotID() {
        return resourceSpotID;
    }

    public int getNbInteraction() {
        return nbInteraction;
    }

    public LocalDateTime getLastInteractionDate() {
        return lastInteractionDate;
    }

    public void syncFromDomain(ProfileRSInteraction domain) {
        this.nbInteraction = domain.getNbInteraction();
        this.lastInteractionDate = domain.getLastInteractionDate();
    }
}
