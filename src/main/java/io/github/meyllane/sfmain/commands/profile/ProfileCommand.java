package io.github.meyllane.sfmain.commands.profile;


import dev.jorel.commandapi.AbstractArgumentTree;
import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.database.entities.Profile;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletionException;

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

            profileService.createAndFlush(profileName)
                    .thenApply(profile -> {
                        profileService.register(profile);
                        return profile;
                    })
                    .whenComplete((profile, ex) -> handleCompletion(sender, profile, ex));
        });
    }

    protected static void handleCompletion(Player sender, Profile profile, Throwable ex) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (ex != null) {
                PluginCommandHelper.handleErrors(ex, sender);
                return;
            }

            sender.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Création du profile " + profile.getName() + " réussie !",
                    PluginMessageType.SUCCESS
            ));
        });
    }
}
