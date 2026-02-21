package io.github.meyllane.sfmain.commands.profile.update;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ProfileArgument;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.AgeProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.NameProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.ProfileTraitUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.profile.update.subcommands.SpeciesProfileUpdateCommandHandler;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.named_elements.SpeciesElement;
import io.github.meyllane.sfmain.registries.NamedElementRegistry;
import io.github.meyllane.sfmain.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.concurrent.CompletableFuture;

public class ProfileUpdateCommand {
    public static final String UPDATE_VALUE_NODE_NAME = "updateValue";
    public static final String UPDATE_OPERATION_NODE_NAME = "updateOperation";
    public static final String PROFILE_NODE_NAME = "profile";
    private static final Logger log = LoggerFactory.getLogger(ProfileUpdateCommand.class);
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    private static final ProfileService profileService = SFMain.profileService;
    private final NamedElementRegistry<SpeciesElement> speciesRegistry = SFMain.speciesRegistry;

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
    ) throws WrapperCommandSyntaxException {
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

            System.out.println(String.valueOf(profile.getProfileTraits()));

            sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Mise à jour de " + profile.getName() + " réussie !",
                    PluginMessageType.SUCCESS
            ));
        });
    }
}
