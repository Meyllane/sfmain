package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class ProfileArgument extends CustomArgument<Profile, String> {

    public ProfileArgument(String nodeName) {
        super(
                new StringArgument(nodeName),
                ProfileArgument::parse
        );

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return SFMain.profileRegistry.getKeys().toArray(String[]::new);
        }));
    }

    private static Profile parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        Profile profile = SFMain.profileRegistry.get(info.input());

        if (profile == null) {
            throw CustomArgumentException.fromAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("profile.unknown"),
                    PluginMessageType.ERROR
            ));
        }

        return profile;
    }
}
