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
import io.github.meyllane.sfmain.registries.ProfileRegistry;
import io.github.meyllane.sfmain.services.ProfileService;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ProfileCommand {
    private final SFMain plugin;
    private final ProfileService profileService;
    private final ProfileRegistry profileRegistry;

    public ProfileCommand(SFMain plugin, ProfileService profileService, ProfileRegistry profileRegistry) {
        this.plugin = plugin;
        this.profileService = profileService;
        this.profileRegistry = profileRegistry;
    }

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

    public void createProfile(CommandSender sender, CommandArguments args) {
        String name = args.getByClass("profileName", String.class);

        profileService.create(name)
                .whenComplete((profile, throwable) ->
                        Bukkit.getScheduler().runTask(plugin, () -> {

                            if (!(sender instanceof Player player)) {
                                return;
                            }

                            if (throwable != null) {
                                player.sendMessage(Component.text(throwable.getCause().getMessage()));
                                return;
                            }

                            profileRegistry.register(profile);
                            player.sendMessage(Component.text(
                                    "Le profile " + profile.getName() + " a bien été créé!"
                            ));
                        })
                );
    }
}
