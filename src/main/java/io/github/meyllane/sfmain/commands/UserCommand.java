package io.github.meyllane.sfmain.commands;

import dev.jorel.commandapi.CommandAPIBukkit;
import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.services.ProfileService;
import io.github.meyllane.sfmain.application.services.UserService;
import io.github.meyllane.sfmain.commands.arguments.ProfileArgument;
import io.github.meyllane.sfmain.commands.arguments.UserArgument;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public class UserCommand {
    private static final SFMain plugin = SFMain.getPlugin(SFMain.class);
    private static final ProfileService profileService = SFMain.profileService;
    private static final UserService userService = SFMain.userService;

    private static final String USER_OPERATION_NODE_NAME = "userOperation";
    private static final String USER_ADD_PROFILE_NODE_VALUE = "add_profile";
    private static final String USER_REMOVE_PROFILE_NODE_VALUE = "remove_profile";

    public static void register() {
        Argument<String> giveProfile = new MultiLiteralArgument(
                USER_OPERATION_NODE_NAME, USER_ADD_PROFILE_NODE_VALUE, USER_REMOVE_PROFILE_NODE_VALUE)
                .thenNested(
                        new ProfileArgument("profile")
                                .executesPlayer(UserCommand::updateOwnedProfiles)
                );

        Argument<String> setActiveProfile = new LiteralArgument("set_active_profile")
                .then(
                        new ProfileArgument("profile")
                                .executesPlayer(UserCommand::setActiveProfile)
                );

        new CommandTree("user")
                .then(
                        new UserArgument("user")
                                .then(giveProfile)
                                .then(setActiveProfile)
                )
                .register();
    }

    public static void updateOwnedProfiles(Player player, CommandArguments args) {
        User user = args.getByClass("user", User.class);
        Profile profile = args.getByClass("profile", Profile.class);
        String operation = args.getByClassOrDefault(USER_OPERATION_NODE_NAME, String.class, "");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            CompletableFuture.supplyAsync(() -> {
                        switch (operation) {
                            case USER_ADD_PROFILE_NODE_VALUE -> user.addProfile(profile);
                            case USER_REMOVE_PROFILE_NODE_VALUE -> user.removeProfile(profile);
                            default -> throw new RuntimeException("Illegal operation.");
                        }

                        userService.update(user);
                        return user;
                    })
                    .whenComplete((updatedUser, ex) -> handleCompletion(player, updatedUser, ex));
        });
    }

    public static void setActiveProfile(Player player, CommandArguments args) {
        User user = args.getByClass("user", User.class);
        Profile profile = args.getByClass("profile", Profile.class);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            CompletableFuture.supplyAsync(() -> {
                        user.setActiveProfile(profile);
                        userService.update(user);
                        return user;
                    })
                    .whenComplete((updatedUser, ex) -> handleCompletion(player, updatedUser, ex));
        });
    }

    protected static void handleCompletion(Player player, User updatedUser, Throwable ex) {
        if (ex != null) {
            PluginCommandHelper.handleErrors(ex, player);
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "L'utilisateur " + updatedUser.getMinecraftName() + " a bien été mis à jour !",
                    PluginMessageType.SUCCESS
            ));
        });
    }
}
