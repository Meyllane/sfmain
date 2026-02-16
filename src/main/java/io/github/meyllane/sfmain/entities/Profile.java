package io.github.meyllane.sfmain.entities;

import io.github.meyllane.sfmain.database.converters.SpeciesConverter;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @Column(name = "description")
    private String description;

    @Column(name = "species_ID")
    @Convert(converter = SpeciesConverter.class)
    private SpeciesElement speciesElement;

    @OneToMany(mappedBy = ProfileTrait_.PROFILE, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Collection<ProfileTrait> profileTraits = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public Profile() {}

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSpeciesElement(SpeciesElement speciesElement) {
        this.speciesElement = speciesElement;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDescription() {
        return description;
    }

    public SpeciesElement getSpeciesElement() {
        return speciesElement;
    }

    public Collection<ProfileTrait> getProfileTraits() {
        return profileTraits;
    }

    public void setProfileTraits(Collection<ProfileTrait> profileTraits) {
        this.profileTraits = profileTraits;
    }
}
