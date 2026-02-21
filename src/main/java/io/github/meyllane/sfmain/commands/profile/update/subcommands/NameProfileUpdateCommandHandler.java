package io.github.meyllane.sfmain.commands.profile.update.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.database.entities.Profile;

public class NameProfileUpdateCommandHandler extends ProfileUpdateCommandHandler<String> {
    @Override
    public LiteralArgument buildBranch() {
        return (LiteralArgument) new LiteralArgument("name")
                .then(
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    String parse(CommandArguments args) {
        String name = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME, String.class, "");
        if (name.isEmpty()) throw new RuntimeException("Le nouveau nom est invalide");
        return name;
    }

    @Override
    protected void update(Profile profile, String updateValue, ProfileUpdateOperation operation) {
        if (operation == ProfileUpdateOperation.UPDATE) {
            profile.setName(updateValue);
            return;
        }

        throw new RuntimeException("Illegal update operation");
    }
}
