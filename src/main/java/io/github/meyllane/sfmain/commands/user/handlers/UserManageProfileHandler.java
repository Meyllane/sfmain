package io.github.meyllane.sfmain.commands.user.handlers;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.models.CommandOperation;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.commands.arguments.ProfileArgument;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.User;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.entity.Player;

import java.util.Objects;

public class UserManageProfileHandler extends ModelUpdateCommandHandler<User, Profile> {
    private final String SET_ACTIVE_PROFILE_ALIAS = "set_active_profile";
    private final String PROFILE_NODE_NAME = "profile";
    private final String OPERATION_NODE = "updateOperation";

    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument(OPERATION_NODE,
                CommandOperation.ADD.getName(),
                CommandOperation.REMOVE.getName(),
                SET_ACTIVE_PROFILE_ALIAS
        ).then(
                new ProfileArgument(PROFILE_NODE_NAME)
                        .executesPlayer(this::execute)
        );
    }

    @Override
    public CommandOperation getOperation(CommandArguments args) {
        String operationName = args.getByClassOrDefault(OPERATION_NODE, String.class, "");

        if (operationName.equals(SET_ACTIVE_PROFILE_ALIAS)) operationName = CommandOperation.SET.getName();

        return CommandOperation.getByName(operationName);
    }

    @Override
    public User persist(User target) {
        return SFMain.userEntityRepository.update(target);
    }

    @Override
    public Profile parse(CommandArguments args) {
        return Objects.requireNonNull(args.getByClass(PROFILE_NODE_NAME, Profile.class));
    }

    @Override
    public void update(User target, Profile value, CommandOperation operation) {
        switch (operation) {
            case ADD -> target.addProfile(value);
            case REMOVE -> target.removeProfile(value);
            case SET -> target.setActiveProfile(value);
            default -> throw new RuntimeException("Illegal CommandOperation(" + operation.getName() + ") in " + this.getClass());
        }
    }

    @Override
    protected Class<User> getTargetClass() {
        return User.class;
    }

    @Override
    public void handleCompletion(ModelUpdateCommandResult<User, Profile> result, Player player) {
        String message = switch (result.operation()) {
            case ADD -> "Le profile %s a bien été ajouté à l'utilisateur %s !";
            case REMOVE -> "Le profile %s a bien été retiré à l'utilsateur %s !";
            case SET -> "Le profile %s est désormais le profile actif de l'utilisateur.rice %s !";
        };

        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                String.format(message, result.updateValue().getName(), result.target().getMinecraftName()),
                PluginMessageType.SUCCESS
        ));
    }
}
