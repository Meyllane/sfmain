package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.ProfileTrait;
import io.github.meyllane.sfmain.errors.SFException;
import io.github.meyllane.sfmain.elements.TraitElement;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class ProfileTraitUpdateCommandHandler extends ProfileUpdateCommandHandler<ProfileTrait> {
    private final ElementRegistry<TraitElement> traitsRegistry = SFMain.traitsRegistry;
    private final String SPECIALIZATION_UPDATE_NODE_NAME = "specialization";
    private final String SPECIALIZATION_VALUE_NODE_NAME = "specializationValue";

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("traits")
                .thenNested(
                        new MultiLiteralArgument(
                                ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME,
                                ProfileUpdateOperation.ADD.getName(),
                                ProfileUpdateOperation.REMOVE.getName()
                        ),
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME).replaceSuggestions(
                                processSuggestions()
                        )
                                .executesPlayer(this::execute)
                )
                .thenNested(
                        new LiteralArgument(SPECIALIZATION_UPDATE_NODE_NAME),
                        new StringArgument(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME).replaceSuggestions(
                                ArgumentSuggestions.strings(info -> {
                                    Profile profile = info.previousArgs().getByClass(ProfileUpdateCommand.PROFILE_NODE_NAME, Profile.class);
                                    if (profile == null) return new String[0];

                                    return getOwnedTraitNames(profile);
                                })
                        ),
                        new GreedyStringArgument(SPECIALIZATION_VALUE_NODE_NAME)
                                .executesPlayer(this::execute)
                );
    }

    private ArgumentSuggestions<CommandSender> processSuggestions() {
        return ArgumentSuggestions.strings(info -> {
            Profile profile = info.previousArgs().getByClass(ProfileUpdateCommand.PROFILE_NODE_NAME, Profile.class);
            String operationName = info.previousArgs().getByClassOrDefault(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, String.class, "");
            ProfileUpdateOperation operation = ProfileUpdateOperation.getByName(operationName);

            if (profile == null) return new String[0];

            return switch(operation) {
                case ADD -> getMissingTraitNames(profile);
                case REMOVE -> getOwnedTraitNames(profile);
                case UPDATE, SET -> null;
            };
        });
    }

    private String @NonNull [] getOwnedTraitNames(Profile profile) {
        return profile.getProfileTraitsName().toArray(new String[0]);
    }

    private String @NonNull [] getMissingTraitNames(Profile profile) {
        List<String> profileTraitsName = profile.getProfileTraitsName();

        return traitsRegistry.getNames().stream()
                .filter(name -> !profileTraitsName.contains(name))
                .toArray(String[]::new);
    }

    @Override
    ProfileTrait parse(CommandArguments args) {
        String traitName = args.getByClassOrDefault(ProfileUpdateCommand.UPDATE_VALUE_NODE_NAME, String.class, "");
        String specialization = args.getByClassOrDefault(SPECIALIZATION_VALUE_NODE_NAME, String.class, "");

        if (traitName.isEmpty()) {
            throw new SFException("Le nom du trait n'est pas reconnu");
        }

        TraitElement trait = traitsRegistry.getByName(traitName);

        if (trait == null) {
            throw new SFException("Le nom du trait n'est pas reconnu");
        }

        return new ProfileTrait(trait, specialization);
    }

    @Override
    protected void update(Profile profile, ProfileTrait updateValue, ProfileUpdateOperation operation) {
        switch (operation) {
            case ADD -> profile.addProfileTrait(updateValue);
            case REMOVE -> profile.removeProfileTrait(updateValue);
            case UPDATE -> profile.updateProfileTrait(updateValue);
        }
    }
}
