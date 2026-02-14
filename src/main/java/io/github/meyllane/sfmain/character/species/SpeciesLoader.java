package io.github.meyllane.sfmain.character.species;

import io.github.meyllane.sfmain.SFMain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class SpeciesLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    private static HashMap<String, Species> loadSpecies(YamlConfiguration config) {
        HashMap<String, Species> map = new HashMap<>();

        List<Map<?, ?>> candidate = config.getMapList("species");

        for (Map<?, ?> elem : candidate) {
            Species spe = new Species(
                    (int) elem.get("ID"),
                    (String) elem.get("name")
            );

            map.put(spe.name(), spe);
        }

        plugin.getLogger().log(Level.INFO, "Loaded " + map.size() + " Species.");

        return map;
    }

    public static void load(YamlConfiguration config) {
        SFMain.speciesMap = SpeciesLoader.loadSpecies(config);
    }
}
