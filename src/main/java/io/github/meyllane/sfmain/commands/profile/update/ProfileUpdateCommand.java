package io.github.meyllane.sfmain.commands.profile.update;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.AgeProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.NameProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.ProfileTraitUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.SpeciesProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import io.github.meyllane.sfmain.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletionException;

public class ProfileUpdateCommand {
    public static final String UPDATE_VALUE_NODE_NAME = "updateValue";
    public static final String UPDATE_OPERATION_NODE_NAME = "updateOperation";
    private static final Logger log = LoggerFactory.getLogger(ProfileUpdateCommand.class);
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    private static final ProfileService profileService = SFMain.profileService;
    private final NamedElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

    public LiteralArgument build() {

        return (LiteralArgument) new LiteralArgument("update")
                .withPermission("sfmain.profile.update")
                .then(
                        new StringArgument("profileName")
                                .replaceSuggestions(
                                        ArgumentSuggestions.stringsAsync(info ->
                                                profileService.getProfileNames()
                                        )
                                )
                                .then(new AgeProfileUpdateCommandHandler().buildBranch())
                                .then(new NameProfileUpdateCommandHandler().buildBranch())
                                .then(new ProfileTraitUpdateCommandHandler().buildBranch())
                                .then(new SpeciesProfileUpdateCommandHandler().buildBranch())
                );
    }

    private static String requireArgument(CommandArguments args) throws WrapperCommandSyntaxException {
        String value = args.getByClassOrDefault("profileName", String.class, "");

        if (value.isEmpty()) {
            throw CommandAPIBukkit.failWithAdventureComponent(PluginMessageHandler.buildPluginMessageComponent(
                    "Le profile demandé n'existe pas",
                    PluginMessageType.ERROR
            ));
        }

        return value;
    }

    public static <T> void handleUpdate(
            Player player,
            CommandArguments args,
            T updateValue,
            ProfileUpdateOperation operation,
            TriConsumer<Profile, T, ProfileUpdateOperation> updater
            ) throws WrapperCommandSyntaxException {

        String profileName = requireArgument(
                args
        );

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            profileService.getProfile(profileName).thenApply(profile -> {
               updater.accept(profile, updateValue, operation);
               profileService.update(profile);
               return profile;
            }).whenComplete((profile, ex) -> handleCompletion(player, profile, ex));
        });
    }

    protected static void handleCompletion(Player sender, Profile profile, Throwable ex) {
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

    private static void handleErrors(Throwable ex, Player sender) {
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
