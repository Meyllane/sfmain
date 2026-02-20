package io.github.meyllane.sfmain.commands.profile.update.updaters;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.ProfileCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;

public class AgeProfileUpdater implements ProfileUpdater<Integer>{
    @Override
    public Integer parse(CommandArguments args) {
        try {
            return Integer.parseInt(args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_ARGUMENT_NAME, String.class, ""));
        } catch (NumberFormatException e) {
            throw new SFException("L'âge doit être un nombre entier.", e);
        }
    }

    @Override
    public void apply(Profile profile, Integer updateValue) {profile.setAge(updateValue);
    }
}
