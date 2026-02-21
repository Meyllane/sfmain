package io.github.meyllane.sfmain.commands.profile.update.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.database.entities.Profile;

public class AgeProfileUpdateCommandHandler extends ProfileUpdateCommandHandler<Integer> {
    @Override
    public LiteralArgument buildBranch() {
        return (LiteralArgument) new LiteralArgument("age")
                .then(
                        new IntegerArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    Integer parse(CommandArguments args) {
        return args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME, Integer.class, 0);
    }

    @Override
    protected void update(Profile profile, Integer updateValue, ProfileUpdateOperation operation) {
        if (operation == ProfileUpdateOperation.UPDATE) {
            profile.setAge(updateValue);
            return;
        }

        throw new RuntimeException("Illegal update operation " + operation.name() + " on " + this.getClass().getName());
    }
}
