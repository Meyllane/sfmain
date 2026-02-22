package io.github.meyllane.sfmain.application.services;

import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.persistence.database.entities.UserEntity;
import io.github.meyllane.sfmain.application.registries.UserRegistry;
import io.github.meyllane.sfmain.persistence.database.repositories.UserRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserService {
    private final UserRepository repository;
    private final UserRegistry registry;

    public UserService(UserRepository repo, UserRegistry registry) {
        this.repository = repo;
        this.registry = registry;
    }

    public void loadAllUsers() {
        registry.register(repository.getAllUsers().toArray(User[]::new));
    }

    public void register(User ...users) {
        registry.register(users);
    }

    public User getUser(UUID minecraftUUID) {
        return registry.getUser(minecraftUUID);
    }

    public User getUser(String minecraftName) { return registry.getUser(minecraftName); }

    public User create(UUID minecraftUUID, String minecraftName) {
        return repository.createUser(minecraftUUID, minecraftName);
    }

    public Set<String> getUserMinecraftNames() {
        return this.registry.getUserMinecraftNames();
    }

    public void update(User user) {
        repository.update(user);
    }
 }
