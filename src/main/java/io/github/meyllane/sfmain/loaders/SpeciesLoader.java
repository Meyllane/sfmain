package io.github.meyllane.sfmain.loaders;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SpeciesLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    public static void load(YamlConfiguration config) {
        List<Map<?, ?>> candidate = config.getMapList("species");

        for (Map<?, ?> elem : candidate) {
            SpeciesElement spe = new SpeciesElement(
                    (int) elem.get("ID"),
                    (String) elem.get("name")
            );
            SFMain.speciesRegistry.register(spe);
        }

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.speciesRegistry.count() + " Species.");
    }
}
