package io.github.meyllane.sfmain.events;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.domain.models.User;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {
    private final SFMain plugin;

    public PlayerJoinEventListener(SFMain plugin) {
        this.plugin = plugin;
    }

    //TODO: Handle User update if the name of the minecraftaccount has changed. Just need to update the regi and persit on join
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        //Quickly remove the join message first
        event.joinMessage(null);
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            Throwable ex = null;
            User user = null;

            try {
                user = SFMain.userRegistry.get(player.getUniqueId().toString());

                if (user == null) {
                    user = SFMain.userEntityRepository.create(new User(
                            player.getUniqueId().toString(), player.getName()
                    ));
                    SFMain.userRegistry.register(user);
                }
            } catch (Exception e) {
                ex = e;
            }

            final Throwable finalEx = ex;
            final User finalUser = user;

            Bukkit.getScheduler().runTask(plugin, () -> {
                this.handleCompletion(finalUser, finalEx, player);
            });

        });
    }

    public void handleCompletion(User user, Throwable ex, Player player) {
        if (ex != null) {
            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    ErrorMessage.get("general.unexpected_error"),
                    PluginMessageType.ERROR
            ));
            return;
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
