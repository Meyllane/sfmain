package io.github.meyllane.sfmain.events;

import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.services.UserService;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

        Bukkit.getScheduler().runTaskAsynchronously(plugin, bukkitTask -> {
            userService.handleJoin(event.getPlayer()).thenAccept(user -> {
                Bukkit.getScheduler().runTask(plugin, bukkitTask1 -> {
                    event.getPlayer().sendMessage(userService.buildJoinMessage(user));
                });
            });
        });
    }
}
