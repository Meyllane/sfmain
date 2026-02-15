package io.github.meyllane.sfmain.loaders;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.named_elements.MasteryElement;
import io.github.meyllane.sfmain.named_elements.MasterySpecializationElement;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;
import java.util.logging.Level;

public class MasteryLoader {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);

    private static void loadMasteries(YamlConfiguration config) {

        List<Map<?, ?>> candidate = config.getMapList("masteries");

        for (Map<?, ?> elem : candidate) {
            MasteryElement masteryElement = new MasteryElement(
                    (int) elem.get("ID"),
                    (String) elem.get("name")
            );

            SFMain.masteriesRegistry.register(masteryElement);
        }

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.masteriesRegistry.count() + " Masteries.");
    }

    private static HashMap<Integer, ArrayList<MasterySpecializationElement>> loadMasterySpe(YamlConfiguration config) {
        HashMap<Integer, ArrayList<MasterySpecializationElement>> map = new HashMap<>();

        List<Map<?, ?>> candidate = config.getMapList("mastery_specializations");

        for (Map<?, ?> elem : candidate) {
            MasterySpecializationElement spe = new MasterySpecializationElement(
                    (Integer) elem.get("ID"),
                    (String) elem.get("name"),
                    (Integer) elem.get("mastery_ID")
            );

            if (!map.containsKey(spe.getMasteryId())) {
                map.put(spe.getMasteryId(), new ArrayList<>());
            }

            SFMain.masterySpecializationsRegistry.register(spe);

            map.get(spe.getMasteryId())
                    .add(spe);
        }

        plugin.getLogger().log(Level.INFO, "Registered " + SFMain.masterySpecializationsRegistry.count() + " individual Mastery Specialization.");

        return map;
    }

    private static void attributeMasterySpe(Collection<MasteryElement> masteries, YamlConfiguration config) {
        HashMap<Integer, ArrayList<MasterySpecializationElement>> speMap = loadMasterySpe(config);
        for (MasteryElement masteryElement : masteries) {
            ArrayList<MasterySpecializationElement> specializations = speMap.get(masteryElement.getId());

            if (specializations == null) continue;

            masteryElement.setSpecializations(specializations);
            plugin.getLogger().log(Level.INFO,
                    "Attributed " + specializations.size() + " Specializations to the Mastery " + masteryElement.getName() + "."
            );
        }
    }

    public static void load(YamlConfiguration config) {
        loadMasteries(config);
        attributeMasterySpe(SFMain.masteriesRegistry.values(), config);
    }
}
