package io.github.meyllane.sfmain.commands.profile.update.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;

public class SpeciesProfileUpdateCommandHandler extends ProfileUpdateCommandHandler<SpeciesElement> {
    private final NamedElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

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
