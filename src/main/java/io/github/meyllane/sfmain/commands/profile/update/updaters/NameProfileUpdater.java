package io.github.meyllane.sfmain.commands.profile.update.updaters;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.database.entities.Profile;

public class NameProfileUpdater implements ProfileUpdater<String>{
    @Override
    public String parse(CommandArguments args) {
        String name = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_ARGUMENT_NAME, String.class, "");
        if (name.isEmpty()) throw new RuntimeException("Le nouveau nom est invalide");
        return name;
    }

    @Override
    public void apply(Profile profile, String updateValue) {
        profile.setName(updateValue);
    }
}
