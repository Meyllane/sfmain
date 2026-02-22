package io.github.meyllane.sfmain.application.registries;

import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Centralized registry managing active {@link User} instances.
 * <p>
 * Stores and provides thread-safe access to users via a {@link ConcurrentHashMap},
 * indexed by their Minecraft {@link UUID}.
 * </p>
 */
public class UserRegistry {

    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    /**
     * Retrieves a user by their UUID.
     *
     * @param uuid the unique UUID of the Minecraft player
     * @return the corresponding {@link User} instance, or {@code null} if not found
     */
    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    /**
     * Retrieves a user from a Bukkit {@link Player} instance.
     *
     * @param player the Bukkit player whose associated user is to be retrieved
     * @return the corresponding {@link User} instance, or {@code null} if not found
     */
    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    /**
     * Retrieves a user by their Minecraft account name.
     * <p>
     * This method performs a linear search across all registered users.
     * </p>
     *
     * @param minecraftName the Minecraft account name of the user
     * @return the corresponding {@link User} instance
     * @throws SFException if no user matches the provided name
     */
    public User getUser(String minecraftName) {
        return userMap.values().stream()
                .filter(user -> user.getMinecraftName().equals(minecraftName))
                .findFirst()
                .orElseThrow(() -> new SFException("Aucun utilisateur ne correspond à ce nom de compte minecraft"));
    }

    /**
     * Registers one or more users into the registry.
     * <p>
     * If a user with the same UUID is already present, it will be overwritten.
     * </p>
     *
     * @param users the users to register
     */
    public void register(User... users) {
        for (User user : users) {
            userMap.put(user.getMinecraftUUID(), user);
        }
    }

    /**
     * Returns the set of Minecraft account names of all registered users.
     *
     * @return a {@link Set} containing the Minecraft names of all registered users
     */
    public Set<String> getUserMinecraftNames() {
        return userMap.values().stream()
                .map(User::getMinecraftName)
                .collect(Collectors.toSet());
    }
}