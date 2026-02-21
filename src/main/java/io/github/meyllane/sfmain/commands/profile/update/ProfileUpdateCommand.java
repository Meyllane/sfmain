package io.github.meyllane.sfmain.commands.profile.update;

import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ProfileArgument;
import io.github.meyllane.sfmain.commands.profile.update.handlers.AgeProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.handlers.NameProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.handlers.ProfileTraitUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.handlers.SpeciesProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.elements.SpeciesElement;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import io.github.meyllane.sfmain.utils.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ProfileUpdateCommand {
    public static final String UPDATE_VALUE_NODE_NAME = "updateValue";
    public static final String UPDATE_OPERATION_NODE_NAME = "updateOperation";
    public static final String PROFILE_NODE_NAME = "profile";
    private static final Logger log = LoggerFactory.getLogger(ProfileUpdateCommand.class);
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    private static final ProfileService profileService = SFMain.profileService;
    private final ElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

    public LiteralArgument build() {

        return (LiteralArgument) new LiteralArgument("update")
                .withPermission("sfmain.profile.update")
                .then(
                        new ProfileArgument(PROFILE_NODE_NAME)
                                .then(new AgeProfileUpdateCommandHandler().buildBranch())
                                .then(new NameProfileUpdateCommandHandler().buildBranch())
                                .then(new ProfileTraitUpdateCommandHandler().buildBranch())
                                .then(new SpeciesProfileUpdateCommandHandler().buildBranch())
                );
    }

    public static <T> void handleUpdate(
            Player player,
            CommandArguments args,
            T updateValue,
            ProfileUpdateOperation operation,
            TriConsumer<Profile, T, ProfileUpdateOperation> updater
    ) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            CompletableFuture.supplyAsync(() -> {
                        Profile profile = args.getByClass("profile", Profile.class);

                        updater.accept(profile, updateValue, operation);

                        profileService.update(profile);

                        return profile;
                    })
                    .whenComplete((profile, ex) -> handleCompletion(player, profile, ex));
        });
    }

    protected static void handleCompletion(Player sender, Profile profile, Throwable ex) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (ex != null) {
                PluginCommandHelper.handleErrors(ex, sender);
                return;
            }

            sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Mise à jour de " + profile.getName() + " réussie !",
                    PluginMessageType.SUCCESS
            ));
        });
    }
}
