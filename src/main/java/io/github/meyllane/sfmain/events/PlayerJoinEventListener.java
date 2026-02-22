package io.github.meyllane.sfmain.events;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.services.UserService;
import io.github.meyllane.sfmain.domain.User;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class PlayerJoinEventListener implements Listener {
    private final UserService userService;
    private final SFMain plugin;

    public PlayerJoinEventListener(UserService userService, SFMain plugin) {
        this.userService = userService;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        //Quickly remove the join message first
        event.joinMessage(null);
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            CompletableFuture.supplyAsync(() -> {
                        return userService.getUser(player.getUniqueId());
                    })
                    .thenApply(user -> {
                        if (user == null) {
                            user = userService.create(player.getUniqueId(), player.getName());
                            userService.register(user);
                        } else {
                            user.setMinecraftName(player.getName());
                            userService.update(user);
                        }

                        return user;
                    })
                    .whenComplete((user, ex) -> this.handleCompletion(user, ex, player));
        });
    }

    public void handleCompletion(User user, Throwable ex, Player player) {
        if (ex != null) {
            PluginCommandHelper.handleErrors(ex, player);
        }

        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Bienvenue sur Sans-Frontière, " + user.getMinecraftName() + " !",
                PluginMessageType.INFO
        ));
        String activeProfileName = user.getActiveProfile() != null ?
                user.getActiveProfile().getName() :
                "Aucun";
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Profile actif : " + activeProfileName,
                PluginMessageType.INFO
        ));
    }
}
