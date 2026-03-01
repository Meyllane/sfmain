package io.github.meyllane.sfmain.commands.profile;


import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import io.github.meyllane.sfmain.commands.arguments.ProfileArgument;
import io.github.meyllane.sfmain.commands.arguments.SpeciesArgument;
import io.github.meyllane.sfmain.commands.core.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.handlers.create.ProfileCreateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.handlers.update.*;

public class ProfileCommand {

    //TODO : Don't forget the perms
    public static void register() {
        new CommandTree("profile")
                .then(new ProfileCreateCommandHandler().buildBranch())
                .thenNested(
                        new LiteralArgument("update"),
                        new ProfileArgument(ModelUpdateCommandHandler.TARGET_NODE)
                                .then(new AgeProfileUpdateCommandHandler().buildBranch())
                                .then(new NameProfileUpdateCommandHandler().buildBranch())
                                .then(new SpeciesProfileUpdateCommandHandler().buildBranch())
                                .then(new TraitProfileUpdateCommandHandler().buildBranch())
                                .then(new MasteryProfileUpdateCommandHandler().buildBranch())
                )
                .register();
    }
}
