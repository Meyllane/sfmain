package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.arguments.CustomItemBaseArgument;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.elements.CustomItemBase;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import org.bukkit.entity.Player;

public class ItemBaseRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<CustomItemBase> {
    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument("operation", "item")
                .thenNested(
                        new CustomItemBaseArgument("itemBase")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    protected void update(ResourceSpot data, CustomItemBase updateValue) {
        data.setItemBase(updateValue);
    }

    @Override
    protected CustomItemBase parse(Player player, CommandArguments args) {
        return args.getByClass("itemBase", CustomItemBase.class);
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
