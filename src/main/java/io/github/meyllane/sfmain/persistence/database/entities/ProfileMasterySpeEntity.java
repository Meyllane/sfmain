package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.persistence.database.converters.MasterySpeConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "profile_mastery_specialization")
public class ProfileMasterySpeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    @JoinColumn(name = "profile_mastery_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private ProfileMasteryEntity profileMastery;

    @Convert(converter = MasterySpeConverter.class)
    @Column(name = "specialization_ID")
    private MasterySpeElement masterySpecializationElement;

    public ProfileMasterySpeEntity() {
    }

    public ProfileMasterySpeEntity(MasterySpeElement masterySpecializationElement, ProfileMasteryEntity profileMastery) {
        this.masterySpecializationElement = masterySpecializationElement;
        this.profileMastery = profileMastery;
    }

    public MasterySpeElement getMasterySpecializationElement() {
        return masterySpecializationElement;
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() &&
                ((ProfileMasterySpeEntity) obj).masterySpecializationElement.equals(this.masterySpecializationElement);
    }

    @Override
    public int hashCode() {
        return this.masterySpecializationElement.hashCode();
    }
}
