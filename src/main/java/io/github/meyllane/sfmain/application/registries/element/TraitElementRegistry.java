package io.github.meyllane.sfmain.application.registries.element;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.core.ElementRegistry;
import io.github.meyllane.sfmain.domain.elements.TraitElement;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.logging.Level;

public class TraitElementRegistry extends ElementRegistry<TraitElement> {
    @Override
    public void load(YamlConfiguration config) {
        try {
            config.getMapList("traits")
                    .forEach(elem -> {
                        this.register(new TraitElement(
                                (int) elem.get("ID"),
                                (String) elem.get("name"),
                                (String )elem.get("description")
                        ));
                    });
        } catch (ClassCastException e) {
            SFMain.getPlugin(SFMain.class).getLogger().log(Level.SEVERE, ErrorMessage.get("registry.trait.bad_cast"));
            throw new RuntimeException(e);
        }

        if (this.getSize() == 0) {
            throw new IllegalArgumentException(ErrorMessage.get("registry.trait.empty"));
        }

        SFMain.getPlugin(SFMain.class).getLogger().log(Level.INFO, "Registered " + this.getSize() + " Traits.");
    }
}
