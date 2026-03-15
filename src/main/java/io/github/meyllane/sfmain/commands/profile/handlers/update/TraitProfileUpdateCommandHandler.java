package io.github.meyllane.sfmain.commands.profile.handlers.update;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.arguments.TraitArgument;
import io.github.meyllane.sfmain.commands.core.models.CommandOperation;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandHandler;
import io.github.meyllane.sfmain.commands.core.models.ModelUpdateCommandResult;
import io.github.meyllane.sfmain.domain.elements.TraitElement;
import io.github.meyllane.sfmain.domain.models.Profile;
import io.github.meyllane.sfmain.domain.models.ProfileTrait;
import io.github.meyllane.sfmain.errors.ErrorMessage;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.utils.PluginMessageHandler;
import io.github.meyllane.sfmain.utils.PluginMessageType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class TraitProfileUpdateCommandHandler extends ModelUpdateCommandHandler<Profile, ProfileTrait> {
    private final String UPDATE_OPERATION_NODE = "updateOperation";
    private final String UPDATE_VALUE_NODE = "updateValue";
    private final String SPECIALIZATION_VALUE_NODE = "specialization";
    private final String UPDATE_SPE_ALIAS = "update_spe";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("traits")
                .thenNested(
                        new MultiLiteralArgument(UPDATE_OPERATION_NODE, CommandOperation.ADD.getName()),
                        new TraitArgument(UPDATE_VALUE_NODE).replaceSuggestions(processSuggestions()),
                        new GreedyStringArgument(SPECIALIZATION_VALUE_NODE).setOptional(true)
                                .executesPlayer(this::execute)
                )
                .thenNested(
                        new MultiLiteralArgument(UPDATE_OPERATION_NODE, CommandOperation.REMOVE.getName()),
                        new TextArgument(UPDATE_VALUE_NODE).replaceSuggestions(processSuggestions())
                                .executesPlayer(this::execute)
                );
    }

    private ArgumentSuggestions<CommandSender> processSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            CommandOperation operation = this.getOperation(info.previousArgs());
            Profile profile = Objects.requireNonNull(info.previousArgs().getByClass(ModelUpdateCommandHandler.TARGET_NODE, Profile.class));

            if (operation == CommandOperation.ADD) {
                return SFMain.traitsElementRegistry.getKeys().toArray(String[]::new);
            } else {
                return this.getOwnedTraits(profile);
            }
        });
    }

    private String[] getOwnedTraits(Profile profile) {
        return profile.getProfileTraits().stream()
                .map(ProfileTrait::toString)
                .map(name -> '"' + name + '"')
                .toArray(String[]::new);
    }

    @Override
    public Profile persist(Profile target) {
        return SFMain.profileEntityRepository.update(target);
    }

    private String[] processTraitString(String input) {
        int openIdx = input.indexOf('[');
        int closeIdx = input.indexOf(']');

        if (openIdx == -1 || closeIdx == -1) {
            return new String[]{input, ""};
        }

        String before = input.substring(0, openIdx).stripTrailing();
        String inside = input.substring(openIdx + 1, closeIdx);

        return new String[]{before, inside};
    }

    @Override
    public ProfileTrait parse(CommandArguments args) {
        TraitElement trait;
        CommandOperation operation = this.getOperation(args);
        String specialization = args.getByClassOrDefault(SPECIALIZATION_VALUE_NODE, String.class, "");

        if (operation == CommandOperation.REMOVE) {
            String traitString = args.getByClassOrDefault(UPDATE_VALUE_NODE, String.class, "");
            String[] process = processTraitString(traitString);
            trait = SFMain.traitsElementRegistry.get(process[0]);
            specialization = process[1];
        } else {
            trait = args.getByClass(UPDATE_VALUE_NODE, TraitElement.class);
        }

        return new ProfileTrait(trait, specialization);
    }

    @Override
    public void update(Profile target, ProfileTrait updateValue, CommandOperation operation) {
        switch (operation) {
            case ADD -> target.addProfileTrait(updateValue);
            case REMOVE -> target.removeProfileTrait(updateValue);
            default -> throw new IllegalStateException("Illegal " + operation.getName() + " operation on " + this.getClass().getName());
        }
    }

    @Override
    public CommandOperation getOperation(CommandArguments args) {
        String operationName = args.getByClassOrDefault(UPDATE_OPERATION_NODE, String.class, "");

        if (operationName.isEmpty()) {
            throw new SFException(ErrorMessage.get("command_operation.unknown"));
        }

        return CommandOperation.getByName(operationName);
    }

    @Override
    protected Class<Profile> getTargetClass() {
        return Profile.class;
    }

    @Override
    public void handleCompletion(ModelUpdateCommandResult<Profile, ProfileTrait> result, Player player) {
        if (result.operation() == CommandOperation.ADD) {
            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Le trait " + result.updateValue().toString() + " a bien été ajouté au Profile " + result.target().getName() + " !",
                    PluginMessageType.SUCCESS
            ));
        } else {
            player.sendMessage(PluginMessageHandler.buildPluginMessageComponent(
                    "Le trait " + result.updateValue().toString() + " a bien été retiré au Profile " + result.target().getName() + " !",
                    PluginMessageType.SUCCESS
            ));
        }
    }
}
