package io.github.meyllane.sfmain.commands.profile.update.handlers;

import dev.jorel.commandapi.arguments.*;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.meyllane.sfmain.SFMain;
import io.github.meyllane.sfmain.application.registries.ElementRegistry;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateCommand;
import io.github.meyllane.sfmain.commands.profile.update.ProfileUpdateOperation;
import io.github.meyllane.sfmain.domain.Profile;
import io.github.meyllane.sfmain.domain.ProfileMastery;
import io.github.meyllane.sfmain.elements.MasteryElement;
import io.github.meyllane.sfmain.errors.SFException;

public class ProfileMasteryUpdateCommandHandler extends ProfileUpdateCommandHandler<ProfileMastery> {
    private static final ElementRegistry<MasteryElement> masteriesRegistry = SFMain.masteriesRegistry;

    @Override
    public Argument<String> buildBranch() {
        return new LiteralArgument("mastery")
                .thenNested(
                        new MultiLiteralArgument(ProfileUpdateCommand.UPDATE_OPERATION_NODE_NAME, ProfileUpdateOperation.SET.getName()),
                        new StringArgument("masteryName").replaceSuggestions(
                                ArgumentSuggestions.strings(info -> {
                                    return masteriesRegistry.getNames().toArray(String[]::new);
                                })
                        ),
                        new IntegerArgument("level")
                                .executesPlayer(this::execute)
                );
    }

    @Override
    ProfileMastery parse(CommandArguments args) {
        String masteryName = args.getByClassOrDefault("masteryName", String.class, "");
        int level = args.getByClassOrDefault("level", Integer.class, 1);
        MasteryElement mastery = masteriesRegistry.getByName(masteryName);

        if (mastery == null) {
            throw new SFException("Cette maîtrise n'existe pas.");
        }

        return new ProfileMastery(mastery, level);
    }

    @Override
    protected void update(Profile profile, ProfileMastery updateValue, ProfileUpdateOperation operation) {
        if (operation != ProfileUpdateOperation.SET) {
            throw new RuntimeException("Illegal ProfileUpdateOperation");
        }

        profile.setProfileMastery(updateValue);
    }
}
