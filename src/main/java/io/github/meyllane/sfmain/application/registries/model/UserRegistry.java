package io.github.meyllane.sfmain.application.registries.model;

import io.github.meyllane.sfmain.application.registries.core.ModelRegistry;
import io.github.meyllane.sfmain.domain.models.User;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRegistry extends ModelRegistry<User> {
    private final Map<String, User> byMinecraftName = new ConcurrentHashMap<>();
    @Override
    public void register(User elem) {
        if (this.map.containsKey(elem.getMinecraftUUID())) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("model_registry.user.duplicate_UUID"), elem.getMinecraftUUID(), elem.getMinecraftName()
            ));
        }

        this.map.put(elem.getMinecraftUUID(), elem);
        this.byMinecraftName.put(elem.getMinecraftName(), elem);
    }

    @Override
    public void delete(String minecraftUUID) {
        User user = this.map.get(minecraftUUID);
        if (user == null) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("model_registry.user.missing_UUID"), minecraftUUID
            ));
        }

        this.map.remove(minecraftUUID);
        this.byMinecraftName.remove(user.getMinecraftName());
    }

    public Set<String> getMinecraftNames() {
        return this.map.values().stream()
                .map(User::getMinecraftName)
                .collect(Collectors.toSet());
    }

    public User getByMinecraftName(String minecraftName) {
        return this.byMinecraftName.get(minecraftName);
    }
}