package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.SpeciesElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public class SpeciesElementRegistry extends ElementRegistry<SpeciesElement> {
    @Override
    public void load(YamlConfiguration config) {
        try {
            config.getMapList("species")
                    .forEach(elem -> {
                        this.register(new SpeciesElement(
                                (int) elem.get("ID"),
                                (String) elem.get("name")
                        ));
                    });
        } catch (ClassCastException e) {
            SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, ErrorMessage.get("registry.species.bad_cast"));
            throw new RuntimeException(e);
        }

        if (this.getSize() == 0) {
            throw new IllegalArgumentException(ErrorMessage.get("registry.species.empty"));
        }

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " Species.");
    }
}
