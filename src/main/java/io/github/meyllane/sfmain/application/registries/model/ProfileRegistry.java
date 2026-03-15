package io.github.meyllane.sfmain.application.registries.model;

import io.github.meyllane.sfmain.application.registries.core.ModelRegistry;
import io.github.meyllane.sfmain.application.registries.core.Registry;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.errors.ErrorMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileRegistry extends ModelRegistry<Profile> {
    @Override
    public void register(Profile elem) {
        if (this.map.containsKey(elem.getName())) {
            throw new IllegalArgumentException(
                    String.format(ErrorMessage.get("model_registry.profile.duplicate_key"), elem.getName())
            );
        }

        this.map.put(elem.getName(), elem);
    }

    public void removeProfileRSInteraction(ResourceSpot spot) {
        this.map.values().forEach(profile -> profile.removeProfileRSInteraction(spot));
    }
}
