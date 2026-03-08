package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.arguments.QualityArgument;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.elements.Quality;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import org.bukkit.entity.Player;

public class QualityRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<Quality> {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("quality")
                .thenNested(
                        new QualityArgument("quality")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    protected void update(ResourceSpot data, Quality updateValue) {
        data.setQuality(updateValue);
    }

    @Override
    protected Quality parse(Player player, CommandArguments args) {
        return args.getByClass("quality", Quality.class);
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
