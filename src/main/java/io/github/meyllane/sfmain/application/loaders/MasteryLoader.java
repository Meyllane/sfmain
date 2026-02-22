package io.github.meyllane.sfmain.application.loaders;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.elements.MasterySpeElement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.logging.Level;

public class MasteryLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    private static void loadMasteries(YamlConfiguration config) {
        config.getMapList("masteries").forEach(elem -> {
            SFMain.masteriesRegistry.register(new MasteryElement(
                    (int) elem.get("ID"),
                    (String) elem.get("name")
            ));
        });

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.masteriesRegistry.count() + " Masteries.");
    }

    private static Map<Integer, List<MasterySpeElement>> loadMasterySpe(YamlConfiguration config) {
        Map<Integer, List<MasterySpeElement>> map = new HashMap<>();

        config.getMapList("mastery_specializations").forEach(elem -> {
            MasterySpeElement spe = new MasterySpeElement(
                    (Integer) elem.get("ID"),
                    (String) elem.get("name"),
                    (Integer) elem.get("mastery_ID")
            );

            SFMain.masterySpecializationsRegistry.register(spe);
            map.computeIfAbsent(spe.getMasteryId(), k -> new ArrayList<>()).add(spe);
        });

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.masterySpecializationsRegistry.count() + " individual Mastery Specialization.");

        return map;
    }

    private static void attributeMasterySpe(Collection<MasteryElement> masteries, YamlConfiguration config) {
        Map<Integer, List<MasterySpeElement>> speMap = loadMasterySpe(config);

        masteries.forEach(masteryElement -> {
            List<MasterySpeElement> specializations = speMap.get(masteryElement.getId());
            if (specializations == null) return;

            masteryElement.setSpecializations(new HashSet<>(specializations));
            plugin.getLogger().log(Level.INFO,
                    "Attributed " + specializations.size() + " Specializations to the Mastery " + masteryElement.getName() + ".");
        });
    }

    public static void load(YamlConfiguration config) {
        loadMasteries(config);
        attributeMasterySpe(SFMain.masteriesRegistry.values(), config);
    }
}