package io.github.meyllane.sfmain.commands.resource_spot.handlers.create;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.ResourceSpotCommand;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ResourceSpotCreateCommandHandler extends CommandHandler {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("create")
                .thenNested(
                        new TextArgument("name")
                                .executesPlayer(this::execute)
                );
    }

    public Location getBlockLocation(Player player) {
        return ResourceSpotCommand.getLocation(player);
    }

    public String parseResourceSpotName(CommandArguments args) {
        String name = args.getByClassOrDefault("name", String.class, "");

        if (name.isEmpty()) {
            throw new SFException(ErrorMessage.get("resource_spot.empty_name"));
        }

        if (SFMain.resourceSpotsRegistry.get(name) != null) {
            throw new SFException(ErrorMessage.get("resource_spot.duplicate_name"));
        }

        return name;
    }

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            Location loc = this.getBlockLocation(player);
            String name = this.parseResourceSpotName(args);
            int ID = SFMain.resourceSpotsRegistry.getMaxID() + 1;

            ResourceSpot spot = new ResourceSpot(ID, name, loc);

            SFMain.resourceSpotsRegistry.registerNew(spot);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    SFMain.resourceSpotsRegistry.saveConfig();
                } catch (Exception e) {
                    Bukkit.getScheduler().runTask(plugin, () -> this.handleErrors(e, player));
                    return;
                }

                Bukkit.getScheduler().runTask(plugin, () -> this.handleCompletion(player, spot));
            });
        } catch (Exception e) {
            this.handleErrors(e, player);
        }
    }

    public void handleCompletion(Player player, ResourceSpot spot) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Le point de collecte " + spot.getName() + " a bien été créé !",
                PluginMessageType.SUCCESS
        ));
    }
}
