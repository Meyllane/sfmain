package io.github.meyllane.sfmain.services;

import io.github.meyllane.sfmain.database.entities.User;
import io.github.meyllane.sfmain.registries.UserRegistry;
import io.github.meyllane.sfmain.repositories.UserRepository;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class UserService {
    private final UserRepository repository;
    private final UserRegistry registry;

    public UserService(UserRepository repo, UserRegistry registry) {
        this.repository = repo;
        this.registry = registry;
    }

    public CompletableFuture<User> handleJoin(Player player) {
        User cached = registry.getUser(player);

        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture
                .supplyAsync(
                        () -> repository.getUser(player)
                )
                .thenApply(user -> {
                    if (user != null) {
                        registry.registerUser(user);
                    } else {
                        user = repository.createUser(player);
                    }
                    return user;
                });
    }

    public Component buildJoinMessage(User user) {
        return Component.text("Bienvenue " + user.getMinecraftName() + " !")
                .color(TextColor.fromHexString("#3D89D9"));
    }
}
