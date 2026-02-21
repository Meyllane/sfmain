package io.github.meyllane.sfmain.services;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.repositories.ProfileRepository;
import org.hibernate.HibernateException;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProfileService {
    private final ProfileRepository repository;
    private final ProfileRegistry registry;
    private static final NamedElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

    public ProfileService(ProfileRepository repository, ProfileRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    public void register(Profile ...profiles) {
        registry.register(profiles);
    }

    public void delete(String profileName) {
        registry.delete(profileName);
    }

    public void loadAllProfiles() {
        registry.register(repository.getAllProfiles().toArray(Profile[]::new));
    }

    private Profile create(String profileName) {
        if (exists(profileName)) {
            throw new SFException("Un Profile existant porte déjà ce nom.");
        }

        return new Profile(profileName, speciesRegistry.getById(1));
    }

    public CompletableFuture<Profile> createAndFlush(String profileName) {
        return CompletableFuture.supplyAsync(() -> {
            Profile profile = create(profileName);
            update(profile);

            return profile;
        });
    }

    public boolean exists(String profileName) {
        return registry.contains(profileName);
    }

    public List<String> getProfileNames() {
        return registry.values().stream().map(Profile::getName).toList();
    }

    private CompletableFuture<Profile> loadProfile(String profileName) {
        return CompletableFuture.supplyAsync(() -> {
            Profile profile;
            try {
                profile = repository.getProfile(profileName);
            } catch (Exception e) {
                if (e instanceof HibernateException) {
                    throw new RuntimeException("Le profil demandé n'existe pas.", e);
                }
                e.printStackTrace();
                throw new RuntimeException("Une erreur inattendue est survenue.", e);
            }

            return profile;
        });
    }

/*    public CompletableFuture<Profile> getProfile(String profileName) {
        Profile cached = registry.getProfile(profileName);

        if (cached != null) return CompletableFuture.completedFuture(cached);

        return loadProfile(profileName).thenApply(profile -> {
            registry.register(profile);
            return profile;
        });
    }*/

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
