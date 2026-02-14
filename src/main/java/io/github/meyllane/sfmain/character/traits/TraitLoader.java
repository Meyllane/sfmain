package io.github.meyllane.sfmain.character.traits;

import io.github.meyllane.sfmain.SFMain;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class TraitLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    private static HashMap<String, Trait> loadTraits(YamlConfiguration config) {
        HashMap<String, Trait> map = new HashMap<>();

        List<Map<?, ?>> candidates = config.getMapList("traits");

        for (Map<?, ?> elem : candidates) {
            Trait trait = new Trait(
                    (int) elem.get("ID"),
                    (String) elem.get("name"),
                    (String )elem.get("description")
            );
            map.put(trait.name(), trait);
        }

        plugin.getLogger().log(Level.INFO, "Loaded " + map.size() + " Traits.");

        return map;
    }

    public static void load(YamlConfiguration config) {
        SFMain.traitsMap = TraitLoader.loadTraits(config);
    }
}
