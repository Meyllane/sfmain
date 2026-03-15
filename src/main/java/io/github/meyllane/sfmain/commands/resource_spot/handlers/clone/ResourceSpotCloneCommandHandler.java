package io.github.meyllane.sfmain.commands.resource_spot.handlers.clone;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ResourceSpotArgument;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.ResourceSpotCommand;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ResourceSpotCloneCommandHandler extends CommandHandler {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("clone")
                .thenNested(
                        new ResourceSpotArgument("base"),
                        new TextArgument("targetName")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            ResourceSpot baseSpot = Objects.requireNonNull(args.getByClass("base", ResourceSpot.class));
            Location newSpotLocation = ResourceSpotCommand.getLocation(player);
            String newSpotName = ResourceSpotCommand.parseResourceSpotName(args, "targetName");
            int ID = SFMain.resourceSpotsRegistry.getMaxID() + 1;

            ResourceSpot cloned = new ResourceSpot(
                    ID,
                    newSpotName,
                    newSpotLocation,
                    baseSpot.getItemBase(),
                    baseSpot.getMaxInteraction(),
                    baseSpot.getQuality(),
                    baseSpot.getMasterySpeElement()
            );

            SFMain.resourceSpotsRegistry.registerNew(cloned);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
               try {
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
                "Le point de récolte a bien été copié à l'emplacement indiqué.",
                PluginMessageType.SUCCESS
        ));
    }
}
