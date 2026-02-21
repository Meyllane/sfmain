package io.github.meyllane.sfmain.database.entities;

import io.github.meyllane.sfmain.database.converters.SpeciesConverter;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.NaturalId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    @OneToMany(mappedBy = ProfileTrait_.PROFILE, cascade = {CascadeType.REMOVE, CascadeType.MERGE}, orphanRemoval = true)
    private List<ProfileTrait> profileTraits = new ArrayList<>();

    @JoinColumn(name = "user_ID")
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

    public List<ProfileTrait> getProfileTraits() {
        return profileTraits;
    }

    public void setProfileTraits(List<ProfileTrait> profileTraits) {
        this.profileTraits = profileTraits;
    }

    public void addProfileTrait(TraitElement trait) {
        ProfileTrait newProfileTrait = new ProfileTrait();
        newProfileTrait.setTrait(trait);

        if (profileTraits.contains(newProfileTrait)) {
            throw new SFException("Le profile a déjà le Trait en question");
        }

        newProfileTrait.setProfile(this);
        profileTraits.add(newProfileTrait);
    }

    public void removeProfileTrait(TraitElement trait) {
        ProfileTrait toRemove = new ProfileTrait();
        toRemove.setTrait(trait);

        ProfileTrait elem = profileTraits.stream()
                .filter(profileTrait -> profileTrait.equals(toRemove))
                .findFirst()
                .orElseThrow(() -> new SFException("Impossible de retirer un trait que le Profile n'a pas."));

        profileTraits.remove(elem);
    }

    @Override
    public boolean equals(Object obj) {
        return obj.getClass() == this.getClass() && ((Profile) obj).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
