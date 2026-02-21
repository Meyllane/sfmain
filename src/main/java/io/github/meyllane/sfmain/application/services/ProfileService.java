package io.github.meyllane.sfmain.application.services;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.application.registries.ProfileRegistry;
import io.github.meyllane.sfmain.persistence.database.repositories.ProfileRepository;
import org.hibernate.HibernateException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileService {
    private final ProfileRepository repository;
    private final ProfileRegistry registry;
    private static final ElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

    public ProfileService(ProfileRepository repository, ProfileRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    public void register(Profile...profiles) {
        registry.register(profiles);
    }

    public void delete(String profileName) {
        registry.delete(profileName);
    }

    public void loadAllProfiles() {
        registry.register(repository.getAllProfiles().toArray(Profile[]::new));
    }

    public Profile create(String profileName) {
        if (exists(profileName)) {
            throw new SFException("Un Profile existant porte déjà ce nom.");
        }

        Profile newProfile = new Profile(profileName, speciesRegistry.getById(1));
        return repository.create(newProfile) ;
    }

    public boolean exists(String profileName) {
        return registry.contains(profileName);
    }

    public List<String> getProfileNames() {
        return registry.values().stream().map(Profile::getName).toList();
    }

    public Profile getProfile(String profileName) {
        Profile profile = registry.getProfile(profileName);

        if (profile == null) {
            throw new SFException("Aucun Profile n'existe avec ce nom.");
        }

        return profile;
    }

    public void update(Profile profile) {
        repository.update(profile);
    }
}
