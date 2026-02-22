package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.elements.MasterySpeElement;
import io.github.meyllane.sfmain.errors.SFException;
import org.bukkit.command.CommandSender;

public class ProfileMasterySpeUpdateCommandHandler extends ProfileUpdateCommandHandler<MasterySpeElement> {
    private static final ElementRegistry<MasteryElement> masteriesRegistry = SFMain.masteriesRegistry;
    private static final ElementRegistry<MasterySpeElement> masterySpeRegistry = SFMain.masterySpecializationsRegistry;
    private final String ADD_SPE_NODE_NAME = "add_spe";
    private final String REMOVE_SPE_NODE_NAME = "remove_spe";
    private final String SPE_VALUE_NODE_NAME = "speName";

    @Override
    public Argument<String> buildBranch() {
        return new MultiLiteralArgument(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, ADD_SPE_NODE_NAME, REMOVE_SPE_NODE_NAME)
                .then(
                        new StringArgument(SPE_VALUE_NODE_NAME).replaceSuggestions(
                                processSuggestions()
                        )
                                .executesPlayer(this::execute)
                );
    }

    private ArgumentSuggestions<CommandSender> processSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            Profile profile = info.previousArgs().getByClass(ProfileUpdateCommand.PROFILE_NODE_NAME, Profile.class);
            ProfileUpdateOperation operation = this.getOperation(info.previousArgs());

            if (profile == null || profile.getProfileMastery() == null) return null;

            return switch(operation) {
                case ADD -> getMissingSpe(profile);
                case REMOVE -> getOwnedSpe(profile);
                default -> null;
            };
        });
    }

    //TODO: Yeah having all the spe in the MasteryElement is weird. Might need to change that
    private String[] getMissingSpe(Profile profile) {

        if (profile.getProfileMastery().getMasteryElement().getSpecializations() == null) return null;

        return profile.getProfileMastery().getMasteryElement().getSpecializations().stream()
                .filter(spe -> !profile.getProfileMastery().getMasterySpecializations().contains(spe))
                .map(MasterySpeElement::getName)
                .toArray(String[]::new);
    }

    private String[] getOwnedSpe(Profile profile) {

        if (profile.getProfileMastery().getMasterySpecializations() == null) return null;

        return profile.getProfileMastery().getMasterySpecializations().stream()
                .map(MasterySpeElement::getName)
                .toArray(String[]::new);
    }

    @Override
    MasterySpeElement parse(CommandArguments args) {
        String speName = args.getByClassOrDefault(SPE_VALUE_NODE_NAME, String.class, "");
        MasterySpeElement spe = masterySpeRegistry.getByName(speName);

        if (spe == null) {
            throw new SFException("Cette spécialisation n'existe pas.");
        }

        return spe;
    }

    @Override
    public ProfileUpdateOperation getOperation(CommandArguments args) {
        String customOperationName = args
                .getByClassOrDefault(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, String.class, "");

        return switch (customOperationName) {
            case ADD_SPE_NODE_NAME -> ProfileUpdateOperation.ADD;
            case REMOVE_SPE_NODE_NAME -> ProfileUpdateOperation.REMOVE;
            default -> throw new IllegalStateException("Unknown operation: " + customOperationName);
        };
    }

    @Override
    protected void update(Profile profile, MasterySpeElement updateValue, ProfileUpdateOperation operation) {
        switch (operation) {
            case ADD -> profile.addMasterySpeElement(updateValue);
            case REMOVE -> profile.removeMasterySpeElement(updateValue);
            default -> throw new RuntimeException("Illegal update operation.");
        }
    }
}
