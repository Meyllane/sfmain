package io.github.meyllane.sfmain.application.loaders;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

//TODO: Fail if there is no species set up, as one is the minimum required for the db to have a default value to fall back on
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
