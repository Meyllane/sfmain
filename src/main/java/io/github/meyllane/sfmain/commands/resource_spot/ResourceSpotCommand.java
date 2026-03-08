package io.github.meyllane.sfmain.commands.resource_spot;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.ResourceSpotArgument;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.create.ResourceSpotCreateCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.handlers.update.*;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class ResourceSpotCommand {
    public static void register() {
        new CommandTree("resource_spot")
                .then(new ResourceSpotCreateCommandHandler().buildBranch())
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
    // /rs create <name>
    /*
    Get the name
    Get the location where the player is looking
    Check if the location and/or the name is already taken
    Create
    Save in .yml
     */


    // /rs update <name> name <newName>
    // /rs update <name> quality <newQuality>
    // /rs update <name> location <newLocation< (based on where the player is looking at)
    // /rs update <name> max_interaction <newInt>
    // /rs update <name> item <newCustomBaseItem>
    /*
    Get the ResourceSpot
    Apply update
    save in .yml
     */

    /*
    getResourceSpot
    parse updateValue
    apply updateValue
    save
     */


    // /rs delete <name>
    /*
    Get the ResourceSpot
    Delete from the registry
    save in .yml
     */

    // /rs view <name>
    /*
    Get the ResourceSpot
    Display in chat
     */


    //ResourceSpotArgument

    /*
    *
    * */
}
