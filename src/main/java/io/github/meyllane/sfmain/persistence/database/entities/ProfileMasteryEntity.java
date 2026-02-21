package io.github.meyllane.sfmain.persistence.database.entities;

import io.github.meyllane.sfmain.domain.ProfileMastery;
import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.persistence.database.converters.MasteryConverter;
import jakarta.persistence.*;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj.getClass() == this.getClass() && ((ProfileMasteryEntity) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, masteryElement);
    }

    public void syncFromDomain(ProfileMastery domain) {
        masteryElement = domain.getMasteryElement();
        level = domain.getLevel();
    }
}
