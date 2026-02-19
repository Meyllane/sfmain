package io.github.meyllane.sfmain.commands.profile.update.updaters;

import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.database.entities.Profile;

public interface ProfileUpdater<C> {
    C parse(CommandArguments args);
    void apply(Profile profile, C updateValue);
}
