package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.TextArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

public class NameRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<String> {
    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument("operation", "name")
                .then(
                        new TextArgument("name")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    protected void update(ResourceSpot data, String updateValue) {
        if (SFMain.resourceSpotsRegistry.get(updateValue) != null) {
            throw new SFException(ErrorMessage.get("resource_spot.duplicate_name"));
        }

        SFMain.resourceSpotsRegistry.remove(data);
        data.setName(updateValue);
        SFMain.resourceSpotsRegistry.register(data);
    }

    @Override
    protected String parse(Player player, CommandArguments args) {
        String name = args.getByClassOrDefault("name", String.class, "");

        if (name.isEmpty()) {
            throw new SFException(ErrorMessage.get("resource_spot.empty_name"));
        }

        return name;
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
