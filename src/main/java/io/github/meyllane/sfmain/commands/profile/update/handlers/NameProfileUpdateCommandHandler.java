package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.application.services.ProfileService;

public class NameProfileUpdateCommandHandler extends ProfileUpdateCommandHandler<String> {
    private static final ProfileService profileService = SFMain.profileService;

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("name")
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
            profileService.delete(profile.getName());
            profile.setName(updateValue);
            profileService.register(profile);
            return;
        }

        throw new RuntimeException("Illegal update operation");
    }
}
