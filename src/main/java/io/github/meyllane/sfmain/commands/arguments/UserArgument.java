package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.models.User;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class UserArgument extends CustomArgument<User, String> {

    public UserArgument(String nodeName) {
        super(
                new StringArgument(nodeName),
                UserArgument::parse
        );

        this.replaceSuggestions(
                ArgumentSuggestions.strings(info -> {
                    return SFMain.userRegistry.getMinecraftNames().toArray(String[]::new);
                })
        );
    }

    public static User parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        try {
            return SFMain.userRegistry.getByMinecraftName(info.input());
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
