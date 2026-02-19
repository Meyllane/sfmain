package io.github.meyllane.sfmain.services;

import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.repositories.ProfileRepository;
import org.hibernate.HibernateException;

import java.util.concurrent.CompletableFuture;

public class ProfileService {
    private final ProfileRepository repository;
    private final ProfileRegistry registry;

    public ProfileService(ProfileRepository repository, ProfileRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    public CompletableFuture<Profile> create(String name) {
        return CompletableFuture.supplyAsync(
                        () -> repository.exists(name)
                )
                .thenApply(exists -> {
                    if (exists) {
                        throw new RuntimeException("Un profil portant ce nom existe déjà.");
                    }

                    return repository.create(name);
                });
    }

    public CompletableFuture<String[]> getProfileNames() {
        return CompletableFuture.supplyAsync(repository::getProfileNames);
    }

    public CompletableFuture<Profile> getProfile(String profileName) {
        Profile cached = registry.getProfile(profileName);

        if (cached != null) return CompletableFuture.completedFuture(cached);

        return CompletableFuture.supplyAsync(() -> {

            Profile profile;
            try {
                profile = repository.getProfile(profileName);
            } catch (Exception e) {
                if (e instanceof HibernateException) {
                    throw new RuntimeException("Le profil demandé n'existe pas.");
                }
                e.printStackTrace();
                throw new RuntimeException("Une erreur inattendue est survenue.");
            }

            return profile;
        });
    }
}
