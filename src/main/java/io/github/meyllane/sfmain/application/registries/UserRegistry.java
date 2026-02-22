package io.github.meyllane.sfmain.application.registries;

import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRegistry {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public User getUser(String minecraftName) {
        return userMap.values().stream()
                .filter(user -> user.getMinecraftName().equals(minecraftName))
                .findFirst()
                .orElseThrow(() -> new SFException("Aucun utilisateur ne correspond à ce nom de compte minecraft"));
    }

    public void register(User ...users) {
        for (User user : users) {
            userMap.put(user.getMinecraftUUID(), user);
        }
    }

    public Set<String> getUserMinecraftNames() {
        return userMap.values().stream()
                .map(User::getMinecraftName)
                .collect(Collectors.toSet());
    }
}
