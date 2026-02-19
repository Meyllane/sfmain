package io.github.meyllane.sfmain.commands.profile.update;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.ProfileCommandArgumentHandler;
import io.github.meyllane.sfmain.commands.profile.ProfileUpdateField;
import io.github.meyllane.sfmain.commands.profile.update.updaters.AgeProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.NameProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.ProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.SpeciesProfileUpdater;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.services.ProfileService;

import java.util.HashMap;
import java.util.Map;

public class ProfileUpdateCommand {
    public static final String UPDATE_ARGUMENT_NAME = "argumentName";
    private final SFMain plugin;
    private final ProfileService profileService;
    private final ProfileRegistry profileRegistry;
    private final Map<ProfileUpdateField, ProfileUpdater<?>> updaters = new HashMap<>(
            Map.of(
                    ProfileUpdateField.NAME, new NameProfileUpdater(),
                    ProfileUpdateField.AGE, new AgeProfileUpdater(),
                    ProfileUpdateField.SPECIES, new SpeciesProfileUpdater()
            )
    );

    public ProfileUpdateCommand(SFMain plugin, ProfileService profileService, ProfileRegistry profileRegistry) {
        this.plugin = plugin;
        this.profileService = profileService;
        this.profileRegistry = profileRegistry;
    }

    public CommandAPICommand build() {
        return new CommandAPICommand("update")
                .withArguments(new StringArgument("profileName").replaceSuggestions(
                        ProfileCommandArgumentHandler.handleProfileNamesAsync(profileService)
                ))
                .withArguments(new StringArgument("updateType").replaceSuggestions(
                        ProfileCommandArgumentHandler.getProfileFieldUpdate()
                ))
                .executes((sender, args) -> {return;});
    }
}
