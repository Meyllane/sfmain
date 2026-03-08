package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.MasterySpeArgument;
import io.github.meyllane.sfmain.commands.core.models.CommandOperation;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.domain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class MasterySpeProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, MasterySpeElement> {
    private final String OPERATION_NODE = "operation";
    private final String ADD_SPE_ALIAS = "add_spe";
    private final String REMOVE_SPE_ALIAS = "remove_spe";
    private final String UPDATE_VALUE_NODE = "updateValue";

    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument(OPERATION_NODE, ADD_SPE_ALIAS, REMOVE_SPE_ALIAS)
                .then(
                        new MasterySpeArgument(UPDATE_VALUE_NODE).replaceSuggestions(processSuggestions())
                                .executesPlayer(this::execute)
                );
    }

    private ArgumentSuggestions<CommandSender> processSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            Profile profile = Objects.requireNonNull(info.previousArgs().getByClass(ModelUpdateCommandHandler.TARGET_NODE, Profile.class));
            CommandOperation operation = this.getOperation(info.previousArgs());
            if (profile.getProfileMastery() == null) {
                throw new SFException(ErrorMessage.get("profile.null_mastery"));
            }

            return switch (operation) {
                case ADD -> getCompatibleSpe(profile);
                case REMOVE -> getOwnedSpe(profile);
                default -> throw new IllegalStateException("Illegal operation");
            };
        });
    }

    private String[] getCompatibleSpe(Profile profile) {
        return SFMain.masterySpeElementRegistry.getSpe(profile.getProfileMastery().getMasteryElement())
                .stream().map(MasterySpeElement::getName)
                .toArray(String[]::new);
    }

    private String[] getOwnedSpe(Profile profile) {
        return profile.getProfileMastery().getMasterySpecializations().stream()
                .map(MasterySpeElement::getName)
                .toArray(String[]::new);
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    @Override
    public MasterySpeElement parse(CommandArguments args) {
        return args.getByClass(UPDATE_VALUE_NODE, MasterySpeElement.class);
    }

    @Override
    public void update(Profile target, MasterySpeElement updateValue, CommandOperation operation) {
        switch (operation) {
            case ADD -> target.addMasterySpeElement(updateValue);
            case REMOVE -> target.removeMasterySpeElement(updateValue);
            default -> throw new IllegalStateException("Illegal operation");
        }
    }

    @Override
    public CommandOperation getOperation(CommandArguments args) {
        String operationName = args.getByClassOrDefault(OPERATION_NODE, String.class, "");
        String finalName = switch (operationName) {
            case ADD_SPE_ALIAS -> CommandOperation.ADD.getName();
            case REMOVE_SPE_ALIAS -> CommandOperation.REMOVE.getName();
            default -> operationName;
        };

        return CommandOperation.getByName(finalName);
    }

    @Override
    protected Class<Profile> getTargetClass() {
        return Profile.class;
    }

    @Override
    public void handleCompletion(ModelUpdateCommandResult<Profile, MasterySpeElement> result, Player player) {
        String message;
        if (result.operation() == CommandOperation.ADD) {
            message = String.format("La spécialisation %s a bien été ajouté au Profile %s", result.updateValue().getName(), result.target().getName());
        } else {
            message = String.format("La spécialisation %s a bien été retiré au Profile %s", result.updateValue().getName(), result.target().getName());
        }

        player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                message,
                PluginMessageType.SUCCESS
        ));
    }
}
