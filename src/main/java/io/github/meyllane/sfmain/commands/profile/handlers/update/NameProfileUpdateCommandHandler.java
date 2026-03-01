package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
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

public class NameProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, String> {
    private final String UPDATE_VALUE_NODE = "new_name";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("name")
                .then(
                        new GreedyStringArgument(UPDATE_VALUE_NODE)
                                .executesPlayer(this::execute)
                );
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    @Override
    public String parse(CommandArguments args) {
        String profileName = args.getByClassOrDefault(UPDATE_VALUE_NODE, String.class, "");

        if (profileName.isEmpty()) {
            throw new SFException(ErrorMessage.get("profile.empty_profile_name"));
        }

        return profileName;
    }

    @Override
    public void update(Profile target, String updateValue, CommandOperation operation) {
        SFMain.profileRegistry.delete(target.getName());

        target.setName(updateValue);
        SFMain.profileRegistry.register(target);
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
    public void handleCompletion(ModelUpdateCommandResult<Profile, String> result, Player player) {
        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                "Le nom du Profile a bien été changé en " + result.updateValue() + " !",
                PluginMessageType.SUCCESS
        ));
    }
}
