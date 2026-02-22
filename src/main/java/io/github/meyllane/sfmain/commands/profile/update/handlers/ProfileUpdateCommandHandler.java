package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.utils.PluginCommandHelper;
import org.bukkit.entity.Player;

public abstract class ProfileUpdateCommandHandler<C> {
    public abstract Argument<String> buildBranch();
    abstract C parse(CommandArguments args);
    protected abstract void update(Profile profile, C updateValue, ProfileUpdateOperation operation);

    public ProfileUpdateOperation getOperation(CommandArguments args) {
        String opName = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, String.class, "");

        if (opName.isEmpty()) return ProfileUpdateOperation.UPDATE;

        return ProfileUpdateOperation.getByName(opName);
    }

    //TODO: Check to change so the parser throws inside handleUpdate ? This way, we don't need to have a try catch here
    public void execute(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
        try {
            ProfileUpdateCommand.handleUpdate(
                    player,
                    args,
                    parse(args),
                    this.getOperation(args),
                    this::update
            );
        } catch (Exception e) {
            PluginCommandHelper.handleErrors(e, player);
        }
    }
}
