package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.SpeciesArgument;
import io.github.meyllane.sfmain.commands.core.models.CommandOperation;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.domain.elements.SpeciesElement;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;

public class SpeciesProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, SpeciesElement> {
    private final String UPDATE_VALUE_NODE = "species";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("species")
                .then(
                        new SpeciesArgument(UPDATE_VALUE_NODE)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    @Override
    public SpeciesElement parse(CommandArguments args) {
        return args.getByClass(UPDATE_VALUE_NODE, SpeciesElement.class);
    }

    @Override
    public void update(Profile target, SpeciesElement updateValue, CommandOperation operation) {
        target.setSpeciesElement(updateValue);
    }

    @Override
    public CommandOperation getOperation(CommandArguments args) {
        return CommandOperation.SET;
    }

    @Override
    protected Class<Profile> getTargetClass() {
        return Profile.class;
    }

    @Override
    public void handleCompletion(ModelUpdateCommandResult<Profile, SpeciesElement> result, Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "L'espèce du Profile " + result.target().getName() + " est désormais " + result.updateValue().getName() + " !",
                PluginMessageType.SUCCESS
        ));
    }
}
