package io.github.meyllane.sfmain.registries;

import io.github.meyllane.sfmain.database.entities.User;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserRegistry {
    private final Map<UUID, User> userMap = new ConcurrentHashMap<>();

    public User getUser(UUID uuid) {
        return userMap.get(uuid);
    }

    public User getUser(Player player) {
        return userMap.get(player.getUniqueId());
    }

    public void registerUser(User user) {
        userMap.put(user.getMinecraftUUID(), user);
    }
}
