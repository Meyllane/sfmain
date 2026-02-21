package io.github.meyllane.sfmain.registries;

import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileRegistry {
    private final Map<String, Profile> map = new ConcurrentHashMap<>();

    public Profile getProfile(String profileName) {
        return map.get(profileName);
    }

    public void register(Profile ...profiles) {
        for (Profile p : profiles) {
            map.put(p.getName(), p);
        }
    }

    public void delete(String profileName) {
        if (!map.containsKey(profileName)) {
            throw new RuntimeException("Can't delete a profileName that is not currently a key of the ProfileRegistry.");
        }

        map.remove(profileName);
    }

    public boolean contains(String profileName) {
        return map.values().stream()
                .anyMatch(profile -> profile.getName().equals(profileName));
    }

    public List<Profile> values() {
        return map.values().stream().toList();
    }
}
