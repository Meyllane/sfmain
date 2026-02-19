package io.github.meyllane.sfmain.registries;

import io.github.meyllane.sfmain.database.entities.Profile;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileRegistry {
    private final Map<String, Profile> map = new ConcurrentHashMap<>();

    public Profile getProfile(String profileName) {
        return map.get(profileName);
    }

    public void register(Profile profile) {
        map.put(profile.getName(), profile);
    }
}
