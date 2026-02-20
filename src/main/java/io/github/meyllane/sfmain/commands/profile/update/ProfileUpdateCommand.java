package io.github.meyllane.sfmain.commands.profile.update;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.ProfileCommandArgumentHandler;
import io.github.meyllane.sfmain.commands.profile.ProfileUpdateField;
import io.github.meyllane.sfmain.commands.profile.update.updaters.AgeProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.NameProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.ProfileUpdater;
import io.github.meyllane.sfmain.commands.profile.update.updaters.SpeciesProfileUpdater;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionException;

public class ProfileUpdateCommand {
    public static final String UPDATE_ARGUMENT_NAME = "argumentName";
    private static final Logger log = LoggerFactory.getLogger(ProfileUpdateCommand.class);
    private final SFMain plugin;
    private final ProfileService profileService;
    private final ProfileRegistry profileRegistry;
    private final NamedElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;
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
        CommandAPICommand updateProfileTraits = new CommandAPICommand("profile_traits");

        return new CommandAPICommand("update")
                .withArguments(new StringArgument("profileName").replaceSuggestions(
                        ProfileCommandArgumentHandler.handleProfileNamesAsync(profileService)
                ))
                .withArguments(new MultiLiteralArgument("updateType", ProfileUpdateField.getShortNames().toArray(new String[0])))
                .withArguments(new StringArgument(UPDATE_ARGUMENT_NAME).replaceSuggestions(
                        getUpdateTypeSuggestions()))
                .executesPlayer(this::handleUpdate);
    }

    protected @NonNull ArgumentSuggestions<CommandSender> getUpdateTypeSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            String prevArgs = info.previousArgs().getByClass("updateType", String.class);
            if (prevArgs.equals(ProfileUpdateField.SPECIES.getShortName())) {
                return speciesRegistry.getNames().toArray(new String[0]);
            }

            return new String[0];
        });
    }

    private <C> void applyUpdater(ProfileUpdater<C> updater, Profile profile, CommandArguments args) {
        C result = updater.parse(args);
        updater.apply(profile, result);
    }

    private String requireArgument(String nodeName, String errorMessage, CommandArguments args) throws WrapperCommandSyntaxException {
        String value = args.getByClassOrDefault(nodeName, String.class, "");

        if (value.isEmpty()) {
            throw CommandAPIBukkit.failWithAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    errorMessage,
                    PluginMessageType.ERROR
            ));
        }

        return value;
    }

    public void handleUpdate(Player sender, CommandArguments args) throws WrapperCommandSyntaxException {
        String updateType = requireArgument(
                "updateType",
                "Le type de mise à jour demandée n'est pas reconnu",
                args
        );

        String profileName = requireArgument(
                "profileName",
                "Le profile demandé n'existe pas",
                args
        );

        ProfileUpdateField updateField = ProfileUpdateField.fromShortName(updateType);
        ProfileUpdater<?> updater = updaters.get(updateField);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            profileService.getProfile(profileName)
                    .thenApply(profile -> getProfile(args, profile, updater))
                    .whenComplete((profile, ex) -> handleCompletion(sender, profile, ex));
        });
    }

    private Profile getProfile(CommandArguments args, Profile profile, ProfileUpdater<?> updater) {
        applyUpdater(updater, profile, args);
        profileService.update(profile);
        return profile;
    }

    protected void handleCompletion(Player sender, Profile profile, Throwable ex) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (ex != null) {
                handleErrors(ex, sender);
                return;
            }

            sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Mise à jour de " + profile.getName() + " réussie !",
                    PluginMessageType.SUCCESS
            ));
        });
    }

    private void handleErrors(Throwable ex, Player sender) {
        String message;
        Throwable root = ex instanceof CompletionException ? ex.getCause() : ex;
        if (root instanceof SFException sfex) {
            message = sfex.getMessage();
        } else {
            root.printStackTrace();
            message = "Une erreur inattendue s'est produite.";
        }

        sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                message,
                PluginMessageType.ERROR
        ));
    }
}
