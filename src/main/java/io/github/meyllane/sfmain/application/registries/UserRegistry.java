package io.github.meyllane.sfmain.application.registries;

import io.github.meyllane.sfmain.domain.User;
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

    public void register(User ...users) {
        for (User user : users) {
            userMap.put(user.getMinecraftUUID(), user);
        }
    }
}
