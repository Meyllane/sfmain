package io.github.meyllane.sfmain.commands.profile.update.updaters;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;

public class SpeciesProfileUpdater implements ProfileUpdater<SpeciesElement> {
    private final NamedElementRegistry<SpeciesElement> registry = SFMain.speciesRegistry;
    @Override
    public SpeciesElement parse(CommandArguments args) {
        String speciesName = args.getByClassOrDefault(
                ProfileUpdateCommand.UPDATE_ARGUMENT_NAME,
                String.class,
                ""
        );

        if (speciesName.isEmpty()) throw new RuntimeException("Nom d'espèce incorrecte.");

        SpeciesElement species = registry.getByName(speciesName);

        if (species == null) throw new RuntimeException("Nom d'espèce incorrecte");

        return species;
    }

    @Override
    public void apply(Profile profile, SpeciesElement updateValue) {
        profile.setSpeciesElement(updateValue);
    }
}
