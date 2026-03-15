package io.github.meyllane.sfmain.commands.resource_spot;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ResourceSpotArgument;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.clone.ResourceSpotCloneCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.create.ResourceSpotCreateCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.delete.ResourceSpotDeleteCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.scan.ResourceSpotScanCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.update.*;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.view.ResourceSpotViewCommandHandler;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

//TODO: Add a command to copy the parameters of a spot on another loc (just not the name)
public class ResourceSpotCommand {
    public static void register() {
        new CommandTree("resource_spot")
                .then(new ResourceSpotCreateCommandHandler().buildBranch())
                .then(new ResourceSpotViewCommandHandler().buildBranch())
                .then(new ResourceSpotCloneCommandHandler().buildBranch())
                .then(new ResourceSpotDeleteCommandHandler().buildBranch())
                .then(new ResourceSpotScanCommandHandler().buildBranch())
                .thenNested(
                        new LiteralArgument("update"),
                        new ResourceSpotArgument(ResourceSpotUpdateCommandHandler.DATA_VALUE_NODE)
                                .then(new ItemBaseRSUpdateCommandHandler().buildBranch())
                                .then(new LocationRSUpdateCommandHandler().buildBranch())
                                .then(new MasterySpeRSUpdateCommandHandler().buildBranch())
                                .then(new MaxInteractionRSUpdateCommandHandler().buildBranch())
                                .then(new NameRSUpdateCommandHandler().buildBranch())
                                .then(new QualityRSUpdateCommandHandler().buildBranch())
                )
                .register();
    }

    @NonNull
    public static Location getLocation(Player player) {
        Block block = player.getTargetBlockExact(5);

        if (block == null || block.getBlockData().getMaterial().isAir()) {
            throw new SFException(ErrorMessage.get("resource_spot.empty_block"));
        }

        if (SFMain.resourceSpotsRegistry.getByLocation(block.getLocation()) != null) {
            throw new SFException(ErrorMessage.get("resource_spot.duplicate_location"));
        }

        return block.getLocation();
    }

    public static String parseResourceSpotName(CommandArguments args, String nodeName) {
        String name = args.getByClassOrDefault(nodeName, String.class, "");

        if (name.isEmpty()) {
            throw new SFException(ErrorMessage.get("resource_spot.empty_name"));
        }

        if (SFMain.resourceSpotsRegistry.get(name) != null) {
            throw new SFException(ErrorMessage.get("resource_spot.duplicate_name"));
        }

        return name;
    }
}
