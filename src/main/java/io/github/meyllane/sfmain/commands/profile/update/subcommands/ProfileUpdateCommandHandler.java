package io.github.meyllane.sfmain.commands.profile.update.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.database.entities.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public abstract class ProfileUpdateCommandHandler<C> {
    public abstract LiteralArgument buildBranch();
    abstract C parse(CommandArguments args);
    protected abstract void update(Profile profile, C updateValue, ProfileUpdateOperation operation);

    public ProfileUpdateOperation getOperation(CommandArguments args) {
        String opName = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, String.class, "");

        if (opName.isEmpty()) return ProfileUpdateOperation.UPDATE;

        return ProfileUpdateOperation.getByName(opName);
    }

    public void execute(Player player, CommandArguments args) throws WrapperCommandSyntaxException {
        ProfileUpdateCommand.handleUpdate(
                player,
                args,
                parse(args),
                this.getOperation(args),
                this::update
        );
    }
}
