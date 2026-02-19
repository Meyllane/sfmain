package io.github.meyllane.sfmain.commands.profile.update.updaters;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.database.entities.Profile;

public class AgeProfileUpdater implements ProfileUpdater<Integer>{
    @Override
    public Integer parse(CommandArguments args) {
        return args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_ARGUMENT_NAME, Integer.class, 0);
    }

    @Override
    public void apply(Profile profile, Integer updateValue) {
        profile.setAge(updateValue);
    }
}
