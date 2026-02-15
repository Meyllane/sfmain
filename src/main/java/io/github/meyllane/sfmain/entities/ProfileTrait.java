package io.github.meyllane.sfmain.entities;

import io.github.meyllane.sfmain.database.converters.TraitConverter;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import jakarta.persistence.*;

@Entity
@Table(name = "profile_trait")
public class ProfileTrait {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile profile;

    @Column(name = "trait_id")
    @Convert(converter = TraitConverter.class)
    private TraitElement trait;

    @Column(name = "trait_specialization")
    private String specialization;

    public ProfileTrait() {}

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
