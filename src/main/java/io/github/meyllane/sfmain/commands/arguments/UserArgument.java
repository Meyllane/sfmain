package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.services.UserService;
import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class UserArgument extends CustomArgument<User, String> {
    private static final UserService userService = SFMain.userService;

    public UserArgument(String nodeName) {
        super(
                new StringArgument(nodeName),
                UserArgument::parse
        );

        this.replaceSuggestions(
                ArgumentSuggestions.strings(info -> {
                    return userService.getUserMinecraftNames().toArray(String[]::new);
                })
        );
    }

    public static User parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        try {
            return userService.getUser(info.input());
        } catch (Exception e) {

            if (e instanceof SFException ex) {
                throw CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                        ex.getMessage(),
                        PluginMessageType.ERROR
                ));
            }

            throw new RuntimeException(e);
        }
    }
}
