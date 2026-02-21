package io.github.meyllane.sfmain.commands.arguments;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;

public class ProfileArgument extends CustomArgument<Profile, String> {
    private static final ProfileService profileService = SFMain.profileService;

    public ProfileArgument(String nodeName) {
        super(
                new StringArgument(nodeName),
                ProfileArgument::parse
        );

        this.replaceSuggestions(ArgumentSuggestions.strings(info -> {
            return profileService.getProfileNames().toArray(String[]::new);
        }));
    }

    private static Profile parse(CustomArgumentInfo<String> info) throws CustomArgumentException {
        try {
            return profileService.getProfile(info.input());
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
