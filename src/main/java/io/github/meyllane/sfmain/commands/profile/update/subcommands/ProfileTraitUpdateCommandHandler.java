package io.github.meyllane.sfmain.commands.profile.update.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;

public class ProfileTraitUpdateCommandHandler extends ProfileUpdateCommandHandler<TraitElement> {
    private final NamedElementRegistry<TraitElement> traitsRegistry = SFMain.traitsRegistry;

    @Override
    public LiteralArgument buildBranch() {
        return (LiteralArgument) new LiteralArgument("profile_traits")
                .thenNested(
                        new MultiLiteralArgument(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, "add", "remove"),
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME).replaceSuggestions(
                                ArgumentSuggestions.strings(traitsRegistry.getNames().toArray(String[]::new))
                        )
                                .executesPlayer(this::execute)
                );
    }

    @Override
    TraitElement parse(CommandArguments args) {
        String traitName = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME, String.class, "");
        if (traitName.isEmpty()) {
            throw new SFException("Le nom du trait n'est pas reconnu");
        }

        TraitElement trait = traitsRegistry.getByName(traitName);

        if (trait == null) {
            throw new SFException("Le nom du trait n'est pas reconnu");
        }

        return trait;
    }

    @Override
    protected void update(Profile profile, TraitElement updateValue, ProfileUpdateOperation operation) {
        switch (operation) {
            case ADD -> profile.addProfileTrait(updateValue);
            case REMOVE -> profile.removeProfileTrait(updateValue);
            case UPDATE -> throw new RuntimeException("Illegal update operation");
        }
    }
}
