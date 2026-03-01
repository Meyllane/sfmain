package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.CommandOperation;
import io.github.meyllane.sfmain.commands.core.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;

public class AgeProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, Integer> {
    private final String UPDATE_VALUE_NODE = "new_age";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("age")
                .then(
                        new IntegerArgument(UPDATE_VALUE_NODE)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    @Override
    public Integer parse(CommandArguments args) {
        Integer age = args.getByClassOrDefault(UPDATE_VALUE_NODE, Integer.class, -1);
        if (age < 0) {
            throw new SFException(ErrorMessage.get("profile.invalid_age"));
        }

        return age;
    }

    @Override
    public void update(Profile target, Integer updateValue, CommandOperation operation) {
        target.setAge(updateValue);
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
    public void handleCompletion(ModelUpdateCommandResult<Profile, Integer> result, Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "L'âge du Profile " + result.target().getName() + " a bien été mis à " + result.updateValue() + " ans !",
                PluginMessageType.SUCCESS
        ));
    }
}
