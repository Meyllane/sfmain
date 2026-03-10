package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.CustomItemBase;
import io.github.meyllane.sfmain.domain.elements.CustomToolBase;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class CustomItemRegistry extends ElementRegistry<CustomItemBase> {
    private final Map<String, CustomItemBase> byShortName = new ConcurrentHashMap<>();
    @Override
    public void load(YamlConfiguration config) {
        try {
            config.getMapList("items").forEach(base -> {
                CustomItemBase customItem;

                if (base.get("mastery_specialization") != null) {
                    customItem = this.handleCustomTool(base);
                } else {
                    customItem = this.handleCustomItem(base);
                }

                this.register(customItem);
            });
        } catch (Exception e) {
            if (e instanceof ClassCastException) {
                SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, ErrorMessage.get("registry.custom_item.bad_cast"));
            } else if (e instanceof SFException) {
                SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, e.getMessage());
            }
            throw new RuntimeException(e);
        }

        if (this.getSize() == 0) {
            throw new IllegalArgumentException(ErrorMessage.get("registry.custom_item.empty"));
        }

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " CustomItems.");
    }

    private List<String> processDescription(String rawDescription) {
        return Arrays.stream(rawDescription.split("\\r?\\n"))
                .toList();
    }

    private CustomItemBase handleCustomItem(Map<?, ?> base) {
        return new CustomItemBase(
                (int) base.get("ID"),
                (String) base.get("name"),
                (String) base.get("short_name"),
                this.processDescription((String) base.get("description")),
                Material.getMaterial((String) base.get("material")),
                (String) base.get("texture")
        );
    }

    private CustomToolBase handleCustomTool(Map<?, ?> base) {
        CustomToolBase item = new CustomToolBase(
                (int) base.get("ID"),
                (String) base.get("name"),
                (String) base.get("short_name"),
                this.processDescription((String) base.get("description")),
                Material.getMaterial((String) base.get("material")),
                (String) base.get("texture")
        );

        int masterSpeID = (int) base.get("mastery_specialization");
        MasterySpeElement masterySpe = SFMain.masterySpeElementRegistry.get(masterSpeID);

        if (masterySpe == null) throw new SFException(String.format(
                ErrorMessage.get("registry.custom_item.unknown_mastery_spe_ID"), item.getId()
        ));

        item.setMasterySpeElement(masterySpe);

        return item;
    }

    @Override
    public void register(CustomItemBase elem) {
        super.register(elem);

        if (byShortName.containsKey(elem.getShortName())) {
            throw new IllegalArgumentException(String.format(
                    ErrorMessage.get("custom_item_registry.duplicate_short_name"),
                    elem.getShortName()
            ));
        }

        byShortName.put(elem.getShortName(), elem);
    }

    public CustomItemBase getByShort(String shortName) {
        return this.byShortName.get(shortName);
    }

    public Set<String> getShortNames() {
        return this.byShortName.keySet();
    }
}
