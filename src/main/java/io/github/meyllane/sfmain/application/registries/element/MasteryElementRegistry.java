package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.MasteryElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public class MasteryElementRegistry extends ElementRegistry<MasteryElement> {
    public void load(YamlConfiguration config) {
        try {
            config.getMapList("masteries").forEach(elem -> {
                this.register(new MasteryElement(
                        (int) elem.get("ID"),
                        (String) elem.get("name")
                ));
            });
        } catch (ClassCastException e) {
            SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, ErrorMessage.get("registry.mastery.bad_cast"));
            throw new RuntimeException(e);
        }

        if (this.getSize() == 0) {
            throw new IllegalArgumentException(ErrorMessage.get("registry.mastery.empty"));
        }

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " Masteries.");
    }
}
