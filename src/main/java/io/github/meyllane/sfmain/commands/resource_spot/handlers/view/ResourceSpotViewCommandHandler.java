package io.github.meyllane.sfmain.commands.resource_spot.handlers.view;

import dev.jorel.commandapi.Execution;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.arguments.ResourceSpotArgument;
import io.github.meyllane.sfmain.commands.core.CommandHandler;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ResourceSpotViewCommandHandler extends CommandHandler {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("view")
                .thenNested(
                        new ResourceSpotArgument("resourceSpot")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public void handleExecution(Player player, CommandArguments args, JavaPlugin plugin) {
        try {
            ResourceSpot spot = Objects.requireNonNull(args.getByClass("resourceSpot", ResourceSpot.class));
            player.sendMessage(PluginMessageHandler.getPluginHeaderComponent().append(spot.getViewComponent()));
        } catch (Exception e) {
            this.handleErrors(e, player);
        }
    }
}
