package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.resource_spot.ResourceSpotCommand;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class LocationRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<Location> {
    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument("operation", "location")
                .executesPlayer(this::execute);
    }

    @Override
    protected void update(ResourceSpot data, Location updateValue) {
        if (SFMain.resourceSpotsRegistry.getByLocation(updateValue) != null) {
            throw new SFException(ErrorMessage.get("resource_spot.duplicate_location"));
        }

        SFMain.resourceSpotsRegistry.remove(data);
        data.setLocation(updateValue);
        SFMain.resourceSpotsRegistry.register(data);
    }

    @Override
    protected Location parse(Player player, CommandArguments args) {
        return ResourceSpotCommand.getLocation(player);
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
