package io.github.meyllane.sfmain.commands.resource_spot.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.MasterySpeArgument;
import io.github.meyllane.sfmain.commands.core.data_elements.ResourceSpotUpdateCommandHandler;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.domain.elements.ResourceSpot;
import org.bukkit.entity.Player;

public class MasterySpeRSUpdateCommandHandler extends ResourceSpotUpdateCommandHandler<MasterySpeElement> {
    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("mastery_spe")
                .thenNested(
                        new MasterySpeArgument("mastery_spe").replaceSuggestions(ArgumentSuggestions.strings(info -> {
                            return SFMain.masterySpeElementRegistry.getKeys().toArray(String[]::new);
                        }))
                                .executesPlayer(this::execute)
                );
    }

    @Override
    protected void update(ResourceSpot data, MasterySpeElement updateValue) {
        data.setMasterySpeElement(updateValue);
    }

    @Override
    protected MasterySpeElement parse(Player player, CommandArguments args) {
        return args.getByClass("mastery_spe", MasterySpeElement.class);
    }

    @Override
    protected Class<ResourceSpot> getTargetClass() {
        return ResourceSpot.class;
    }
}
