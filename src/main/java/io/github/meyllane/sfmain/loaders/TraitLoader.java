package io.github.meyllane.sfmain.loaders;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TraitLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    public static void load(YamlConfiguration config) {
        List<Map<?, ?>> candidates = config.getMapList("traits");

        for (Map<?, ?> elem : candidates) {
            TraitElement traitElement = new TraitElement(
                    (int) elem.get("ID"),
                    (String) elem.get("name"),
                    (String )elem.get("description")
            );
            SFMain.traitsRegistry.register(traitElement);
        }

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.traitsRegistry.count() + " Traits.");
    }
}
