package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ResourceSpotRegistry extends ElementRegistry<ResourceSpot> {
    private final Map<Location, ResourceSpot> byLocation = new ConcurrentHashMap<>();
    private YamlConfiguration config;
    private final File configFile;
    private int maxID;

    public ResourceSpotRegistry(File configFile) {
        this.configFile = configFile;
    }

    @Override
    public void register(ResourceSpot elem) {
        super.register(elem);
        if (this.byLocation.containsKey(elem.getLocation())) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("resource_spot_registry.duplicate_location"),
                    elem.getId()
            ));
        }

        this.byLocation.put(elem.getLocation(), elem);
    }

    public void registerNew(ResourceSpot spot) {
        this.register(spot);
        this.maxID++;

        config.set("max_ID", maxID);
        config.set("resource_spots." + spot.getName(), spot);
    }

    public void saveConfig() throws IOException {
        this.config.save(this.configFile);
    }

    @Override
    public void load(YamlConfiguration config) {
        this.config = config;

        this.maxID = config.getInt("max_ID");

        ConfigurationSection section = config.getConfigurationSection("resource_spots");

        if (section == null) return;

        section.getKeys(false).forEach(key -> {
            ResourceSpot spot = (ResourceSpot) config.get("resource_spots." + key);
            this.register(spot);
        });

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " ResourceSpots.");
    }

    public ResourceSpot getByLocation(Location loc) {
        return this.byLocation.get(loc);
    }

    public int getMaxID() {
        return this.maxID;
    }

    public void remove(ResourceSpot elem) {
        this.byName.remove(elem.getName());
        this.byId.remove(elem.getId());
        this.byLocation.remove(elem.getLocation());
    }

    public void delete(ResourceSpot elem) {
        this.remove(elem);

        this.config.set("resource_spots." + elem.getName(), null);
    }

    public Set<ResourceSpot> getSpotsInRange(Location loc, int range) {
        return this.getValues().stream()
                .filter(spot -> spot.getLocation().distance(loc) <= range)
                .collect(Collectors.toSet());
    }
}
