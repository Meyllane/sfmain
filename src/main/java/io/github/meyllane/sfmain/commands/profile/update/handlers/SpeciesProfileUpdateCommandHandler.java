package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.persistence.database.entities.ProfileEntity;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;

public class SpeciesProfileUpdateCommandHandler extends ProfileUpdateCommandHandler<SpeciesElement> {
    private final ElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

    @Override
    public LiteralArgument buildBranch() {
        return (LiteralArgument) new LiteralArgument("species")
                .then(
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME).replaceSuggestions(
                                ArgumentSuggestions.strings(speciesRegistry.getNames().toArray(String[]::new))
                        )
                                .executesPlayer(this::execute)
                );
    }

    @Override
    SpeciesElement parse(CommandArguments args) {
        String speciesName = args.getByClassOrDefault(
                ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME,
                String.class,
                ""
        );

        if (speciesName.isEmpty()) throw new SFException("Nom d'espèce incorrecte.");

        SpeciesElement species = speciesRegistry.getByName(speciesName);

        if (species == null) throw new SFException("Nom d'espèce incorrecte.");

        return species;
    }

    @Override
    protected void update(Profile profile, SpeciesElement updateValue, ProfileUpdateOperation operation) {
        if (operation == ProfileUpdateOperation.UPDATE) {
            profile.setSpeciesElement(updateValue);
            return;
        }

        throw new RuntimeException("Illegal update operation");
    }
}
