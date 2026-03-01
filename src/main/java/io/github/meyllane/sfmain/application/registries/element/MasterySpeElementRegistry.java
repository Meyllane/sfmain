package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class MasterySpeElementRegistry extends ElementRegistry<MasterySpeElement> {
    private final Map<MasteryElement, Set<MasterySpeElement>> byMastery = new ConcurrentHashMap<>();

    public void registerLink(MasterySpeElement elem, MasteryElement mastery) {
        byMastery.computeIfAbsent(mastery, k -> ConcurrentHashMap.newKeySet()).add(elem);
    }

    @Override
    public void load(YamlConfiguration config) {
        try {

            config.getMapList("mastery_specializations")
                    .forEach(this::loadSpeElement);

            if (this.getSize() == 0)
                throw new IllegalArgumentException(ErrorMessage.get("registry.mastery_spe.empty"));

        } catch (ClassCastException e) {
            SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, ErrorMessage.get("registry.mastery_spe.bad_cast"));
            throw new RuntimeException(e);

        } catch (IllegalArgumentException e) {
            SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " Mastery Specializations.");
    }

    private void loadSpeElement(Map<?, ?> elem) {
        MasterySpeElement masterySpeElement = new MasterySpeElement(
                (int) elem.get("ID"),
                (String) elem.get("name"),
                (int) elem.get("mastery_ID")
        );

        MasteryElement parent = SFMain.masteriesElementRegistry.get(masterySpeElement.getMasteryId());

        if (parent == null) {
            String message = String.format(ErrorMessage.get("registry.mastery.unknown_id"), masterySpeElement.getMasteryId());
            throw new IllegalArgumentException(message);
        }

        this.register(masterySpeElement);
        this.registerLink(masterySpeElement, parent);
    }

    public Set<MasterySpeElement> getSpe(MasteryElement masteryElement) {
        return this.byMastery.get(masteryElement);
    }
}
