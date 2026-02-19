package io.github.meyllane.sfmain.commands.profile;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import io.github.meyllane.sfmain.services.ProfileService;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

//TODO : Maybe not useful to put them in an isolated class. Merge into ProfileCommand.java ?
public final class ProfileCommandArgumentHandler {
    public static ArgumentSuggestions<CommandSender> handleProfileNamesAsync(ProfileService service) {
        return ArgumentSuggestions.stringsAsync(info -> {
            return service.getProfileNames();
        });
    }

    public static ArgumentSuggestions<CommandSender> getProfileFieldUpdate() {
        return ArgumentSuggestions.strings(info -> {
            return ProfileUpdateField.getShortNames().toArray(String[]::new);
        });
    }
}
