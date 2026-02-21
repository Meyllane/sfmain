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
import io.github.meyllane.sfmain.database.entities.ProfileTrait;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.TraitElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ProfileTraitUpdateCommandHandler extends ProfileUpdateCommandHandler<TraitElement> {
    private final NamedElementRegistry<TraitElement> traitsRegistry = SFMain.traitsRegistry;

    @Override
    public LiteralArgument buildBranch() {
        return (LiteralArgument) new LiteralArgument("traits")
                .thenNested(
                        new MultiLiteralArgument(
                                ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME,
                                ProfileUpdateOperation.ADD.getName(),
                                ProfileUpdateOperation.REMOVE.getName()
                        ),
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME).replaceSuggestions(
                                processSuggestions()
                        )
                                .executesPlayer(this::execute)
                );
    }

    private ArgumentSuggestions<CommandSender> processSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            Profile profile = info.previousArgs().getByClass(ProfileUpdateCommand.PROFILE_NODE_NAME, Profile.class);
            String operation = info.previousArgs().getByClassOrDefault(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, String.class, "");

            if (profile == null || operation.isEmpty()) return new String[0];

            if (operation.equals(ProfileUpdateOperation.ADD.getName())) { //Show the Traits that the profile does not have
                List<String> profileTraitsName = profile.getProfileTraitsName();

                return traitsRegistry.getNames().stream()
                        .filter(name -> !profileTraitsName.contains(name))
                        .toArray(String[]::new);
            }

            if (operation.equals(ProfileUpdateOperation.REMOVE.getName())) { //Show the Traits that the profile has
                return profile.getProfileTraitsName().toArray(new String[0]);
            }

            return new String[0];
        });
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
