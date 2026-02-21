package io.github.meyllane.sfmain.commands.profile;


import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class ProfileCommand {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    private static final ProfileService profileService = SFMain.profileService;

    //TODO : Don't forget the perms
    public void register() {

        LiteralArgument createProfile = (LiteralArgument) new LiteralArgument("create")
                .withPermission("sfmain.profile.create")
                .then(
                        new GreedyStringArgument("profileName")
                                .executesPlayer(this::createProfile)
                );

        new CommandTree("profile")
                .then(createProfile)
                .then(new ProfileUpdateCommand().build())
                .register();
    }

    public void createProfile(Player sender, CommandArguments args) {
        String profileName = args.getByClass("profileName", String.class);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            CompletableFuture.supplyAsync(() -> {
                        Profile profile = profileService.create(profileName);
                        profileService.register(profile);
                        return profile;
                    })
                    .whenComplete((profile, ex) -> handleCompletion(profile, ex, sender));
        });
    }

    protected static void handleCompletion(Profile profile, Throwable ex, Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (ex != null) {
                PluginCommandHelper.handleErrors(ex, player);
                return;
            }

            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Création du profile " + profile.getName() + " réussie !",
                    PluginMessageType.SUCCESS
            ));
        });
    }
}
