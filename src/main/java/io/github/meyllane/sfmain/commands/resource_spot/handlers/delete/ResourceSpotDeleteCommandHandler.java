package io.github.meyllane.sfmain.commands.resource_spot.handlers.delete;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ResourceSpotArgument;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ResourceSpotDeleteCommandHandler extends CommandHandler {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("delete")
                .then(
                        new ResourceSpotArgument("resourceSpot")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            ResourceSpot spot = Objects.requireNonNull(args.getByClass("resourceSpot", ResourceSpot.class));
            SFMain.resourceSpotsRegistry.delete(spot);
            SFMain.profileRegistry.removeProfileRSInteraction(spot);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    SFMain.profileEntityRepository.deleteProfileRSInteractions(spot);
                    SFMain.resourceSpotsRegistry.saveConfig();
                    Bukkit.getScheduler().runTask(plugin, () -> this.handleCompletion(player));
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
                }
            });
        } catch (Exception e) {
            this.handleErrors(e, player);
        }
    }

    public void handleCompletion(Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Le point de récolte a bien été supprimé.",
                PluginMessageType.SUCCESS
        ));
    }
}
