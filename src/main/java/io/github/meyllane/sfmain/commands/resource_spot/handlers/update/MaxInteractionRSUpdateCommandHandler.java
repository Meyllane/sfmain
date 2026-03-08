package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.entity.Player;

public class MaxInteractionRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<Integer> {
    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument("operation", "max_interaction")
                .thenNested(
                        new IntegerArgument("amount")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    protected void update(ResourceSpot data, Integer updateValue) {
        data.setMaxInteraction(updateValue);
    }

    @Override
    protected Integer parse(Player player, CommandArguments args) {
        int amount = args.getByClassOrDefault("amount", Integer.class, 1);

        if (amount <= 0) {
            throw new SFException(ErrorMessage.get("resource_spot.invalid_max_interaction"));
        }

        return amount;
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
