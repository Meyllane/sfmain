package io.github.meyllane.sfmain.domain.elements;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.models.Profile;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ResourceSpot extends Element implements ConfigurationSerializable {
    private Location location;
    private CustomItemBase itemBase;
    private int maxInteraction;
    private Quality quality;
    private MasterySpeElement masterySpeElement;

    public ResourceSpot(Integer id, String name, Location location) {
        super(id, name);
        this.location = location;
    }

    public ResourceSpot(Integer id, String name, Location location, CustomItemBase itemBase, int maxInteraction,
                        Quality quality, MasterySpeElement masterySpeElement
    ) {
        super(id, name);
        this.location = location;
        this.itemBase = itemBase;
        this.maxInteraction = maxInteraction;
        this.quality = quality;
        this.masterySpeElement = masterySpeElement;
    }

    public Location getLocation() {
        return location;
    }

    public CustomItemBase getItemBase() {
        return itemBase;
    }

    public int getMaxInteraction() {
        return maxInteraction;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setItemBase(CustomItemBase itemBase) {
        this.itemBase = itemBase;
    }

    public void setMaxInteraction(int maxInteraction) {
        this.maxInteraction = maxInteraction;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMasterySpeElement(MasterySpeElement masterySpeElement) {
        this.masterySpeElement = masterySpeElement;
    }

    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        map.put("ID", this.id);
        map.put("name", this.name);
        map.put("location", this.location);
        map.put("item_base_ID", this.itemBase == null ? null : this.itemBase.id);
        map.put("max_interaction", this.maxInteraction);
        map.put("quality_ID", this.quality == null ? null : this.quality.getID());
        map.put("mastery_spe_ID", this.masterySpeElement == null ? null : this.masterySpeElement.getId());

        return map;
    }

    public static ResourceSpot deserialize(Map<String, Object> args) {
        ResourceSpot spot =  new ResourceSpot(
                (int) args.get("ID"),
                (String) args.get("name"),
                (Location) args.get("location")
        );

        if (args.get("item_base_ID") != null) {
            spot.setItemBase(SFMain.customItemsRegistry.get((int) args.get("item_base_ID")));
        }

        if (args.get("max_interaction") != null) {
            spot.setMaxInteraction((int) args.get("max_interaction"));
        }

        if (args.get("quality_ID") != null) {
            spot.setQuality(Quality.getByID((int) args.get("quality_ID")));
        }

        if (args.get("mastery_spe_ID") != null) {
            spot.setMasterySpeElement(SFMain.masterySpeElementRegistry.get((int) args.get("mastery_spe_ID")));
        }

        return spot;
    }

    public boolean isSetup() {
        return this.itemBase != null && this.maxInteraction > 0 && this.quality != null;
    }

    public int getInteractionScore(@Nullable CustomItem customItem, Profile profile) {
        if (this.masterySpeElement == null) return this.quality.getValue();

        if (!(customItem instanceof CustomTool tool)) return this.quality.getValue();

        if (!tool.getMasterySpeElement().equals(this.masterySpeElement)
                || !profile.getProfileMastery().getMasterySpecializations().contains(tool.getMasterySpeElement())
        ) return this.quality.getValue();

        return this.quality.getValue() + tool.quality.getValue() + profile.getProfileMastery().getLevel();
    }
}
