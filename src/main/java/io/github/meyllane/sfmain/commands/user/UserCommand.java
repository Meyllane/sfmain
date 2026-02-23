package io.github.meyllane.sfmain.commands.user;

import dev.jorel.commandapi.CommandTree;
import io.github.meyllane.sfmain.commands.CommandHandler;
import io.github.meyllane.sfmain.commands.arguments.UserArgument;
import io.github.meyllane.sfmain.commands.user.handlers.UserManageProfileHandler;

public class UserCommand {
    public static void register() {
        new CommandTree("user")
                .then(new UserArgument(CommandHandler.TARGET_NODE)
                        .then(new UserManageProfileHandler().buildBranch())
                )
                .register();
    }
}
